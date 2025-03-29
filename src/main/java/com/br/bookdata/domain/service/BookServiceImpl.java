package com.br.bookdata.domain.service;

import static com.br.bookdata.domain.service.BookCacheServiceImpl.mountKeyById;
import static com.br.bookdata.domain.service.BookCacheServiceImpl.mountKeyByPaged;

import com.br.bookdata.domain.exception.BookNotFoundException;
import com.br.bookdata.domain.model.Book;
import com.br.bookdata.domain.observer.contract.ISubject;
import com.br.bookdata.domain.repository.IBookRepository;
import com.br.bookdata.domain.service.contract.IBookCacheService;
import com.br.bookdata.domain.service.contract.IBookService;
import com.br.bookdata.domain.service.enums.BookCacheKeyType;
import com.br.bookdata.domain.utils.CustomPage;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service("bookServiceImpl")
@Log4j2
public class BookServiceImpl implements IBookService<Book, Long> {
  private final IBookRepository repository;
  private final IBookCacheService<Book, Long> bookCacheService;
  private final ISubject<Book> bookObserver;

  public BookServiceImpl(
      IBookRepository repository,
      @Qualifier("bookCacheServiceImpl") IBookCacheService<Book, Long> bookCacheService,
      @Qualifier("bookViewedISubject") ISubject<Book> bookObserver) {
    this.repository = repository;
    this.bookCacheService = bookCacheService;
    this.bookObserver = bookObserver;
  }

  @Override
  public CustomPage<Book> getAllBooks(int page, int size) {
    String cacheKey = mountKeyByPaged(BookCacheKeyType.BOOK_PAGED_KEY, page, size);
    return bookCacheService
        .getAllBooks(page, size)
        .orElseGet(
            () -> {
              var books = repository.findAll(toPageable(page, size));
              var customPage = new CustomPage<>(books);
              bookCacheService.putCache(customPage, cacheKey);
              return customPage;
            });
  }

  @Override
  public CustomPage<Book> getBooksByGenre(String genre, int page, int size) {
    return bookCacheService
        .getBooksByGenre(genre, page, size)
        .orElseGet(
            () -> {
              var booksByGenre = repository.findByGenreIgnoreCase(genre, toPageable(page, size));
              var customPage = new CustomPage<>(booksByGenre);
              bookCacheService.putCache(
                  new CustomPage<>(booksByGenre),
                  mountKeyByPaged(BookCacheKeyType.BOOK_PAGED_GENRE_KEY, page, size, genre));
              return customPage;
            });
  }

  @Override
  public CustomPage<Book> getBooksByAuthor(String author, int page, int size) {
    return bookCacheService
        .getBooksByAuthor(author, page, size)
        .orElseGet(
            () -> {
              var booksByAuthor = repository.findByAuthorIgnoreCase(author, toPageable(page, size));
              var customPage = new CustomPage<>(booksByAuthor);
              bookCacheService.putCache(
                  customPage,
                  mountKeyByPaged(BookCacheKeyType.BOOK_PAGED_AUTHOR_KEY, page, size, author));
              return customPage;
            });
  }

  @Override
  public List<Book> getRecentlyViewed() {
    return bookCacheService.getRecentlyViewed();
  }

  @Override
  public Book getBookById(Long id) {
    var book =
        bookCacheService
            .getBookById(id)
            .orElseGet(
                () ->
                    repository
                        .findById(id)
                        .map(
                            bookEntity -> {
                              bookCacheService.putCache(
                                  bookEntity, mountKeyById(BookCacheKeyType.BOOK_ID_KEY, id));
                              return bookEntity;
                            })
                        .orElseThrow(
                            () ->
                                new BookNotFoundException(
                                    String.format("Book id: %s not found", id))));
    bookObserver.notifyObservers(book);
    return book;
  }

  private PageRequest toPageable(int page, int size) {
    return PageRequest.of(page, size);
  }
}
