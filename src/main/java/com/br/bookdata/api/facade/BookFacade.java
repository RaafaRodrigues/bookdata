package com.br.bookdata.api.facade;

import com.br.bookdata.api.dtos.BookBasicDTO;
import com.br.bookdata.api.dtos.BookDTO;
import com.br.bookdata.api.dtos.mapper.BookMapper;
import com.br.bookdata.domain.model.Book;
import com.br.bookdata.domain.service.contract.IBookService;
import com.br.bookdata.domain.utils.CustomPage;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public final class BookFacade {
  private final IBookService<Book, Long> bookService;

  public BookFacade(@Qualifier("bookServiceImpl") IBookService<Book, Long> bookService) {
    this.bookService = bookService;
  }

  public CustomPage<BookBasicDTO> getAllBooks(int page, int size) {
    return bookService.getAllBooks(page, size).map(BookMapper::toBasicDTO);
  }

  public CustomPage<BookBasicDTO> getBooksByGenre(String genre, int page, int size) {
    return bookService.getBooksByGenre(genre, page, size).map(BookMapper::toBasicDTO);
  }

  public CustomPage<BookBasicDTO> getBooksByAuthor(String author, int page, int size) {
    return bookService.getBooksByAuthor(author, page, size).map(BookMapper::toBasicDTO);
  }

  public List<BookDTO> getRecentlyViewed() {
    return bookService.getRecentlyViewed().stream().map(BookMapper::toDTO).toList();
  }

  public BookDTO getBookById(Long id) {
    return BookMapper.toDTO(bookService.getBookById(id));
  }
}
