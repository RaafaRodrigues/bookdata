package com.br.bookdata.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.br.bookdata.domain.exception.BookNotFoundException;
import com.br.bookdata.domain.model.Book;
import com.br.bookdata.domain.observer.contract.ISubject;
import com.br.bookdata.domain.repository.IBookRepository;
import com.br.bookdata.domain.service.contract.IBookCacheService;
import com.br.bookdata.domain.utils.CustomPage;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

  @Mock private IBookRepository repository;

  @Mock private IBookCacheService<Book, Long> bookCacheService;

  @Mock private ISubject<Book> bookObserver;

  @InjectMocks private BookServiceImpl bookService;

  private Book book;

  @BeforeEach
  void setUp() {
    book =
        Book.builder()
            .title("Title")
            .id(1L)
            .genre("Fiction")
            .author("Test Author")
            .description("Description")
            .build();
  }

  @Test
  @DisplayName("Should return books from cache when available")
  void shouldGetAllBooksCacheHit() {
    int page = 0, size = 10;
    Page<Book> expectedPage = new PageImpl<>(List.of(book));
    when(bookCacheService.getAllBooks(page, size))
        .thenReturn(Optional.of(new CustomPage<>(expectedPage)));

    CustomPage<Book> result = bookService.getAllBooks(page, size);

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    verify(repository, never()).findAll(any(Pageable.class));
  }

  @Test
  @DisplayName("Should fetch books from repository when cache is empty")
  void shouldGetAllBooksCacheMiss() {
    int page = 0, size = 10;
    Page<Book> expectedPage = new PageImpl<>(List.of(book));
    when(bookCacheService.getAllBooks(page, size)).thenReturn(Optional.empty());
    when(repository.findAll(any(Pageable.class))).thenReturn(expectedPage);

    CustomPage<Book> result = bookService.getAllBooks(page, size);

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());

    var responseBook = result.getContent().get(0);
    assertEquals(book.getAuthor(), responseBook.getAuthor());
    assertEquals(book.getId(), responseBook.getId());
    assertEquals(book.getTitle(), responseBook.getTitle());
    assertEquals(book.getGenre(), responseBook.getGenre());
    assertEquals(book.getDescription(), responseBook.getDescription());

    verify(bookCacheService).putCache(any(CustomPage.class), anyString());
  }

  @Test
  @DisplayName("Should return book from cache when available")
  void shouldGetBookByIdCacheHit() {
    when(bookCacheService.getBookById(1L)).thenReturn(Optional.of(book));

    Book result = bookService.getBookById(1L);

    assertNotNull(result);
    assertEquals(book.getId(), result.getId());
    verify(repository, never()).findById(anyLong());
    verify(bookObserver).notifyObservers(book);
  }

  @Test
  @DisplayName("Should fetch book from repository when not found in cache")
  void shouldGetBookByIdCacheMiss() {
    when(bookCacheService.getBookById(1L)).thenReturn(Optional.empty());
    when(repository.findById(1L)).thenReturn(Optional.of(book));

    Book result = bookService.getBookById(1L);

    assertNotNull(result);
    assertEquals(book.getId(), result.getId());

    verify(bookCacheService).putCache(eq(book), anyString());
    verify(bookObserver).notifyObservers(book);
  }

  @Test
  @DisplayName("Should throw BookNotFoundException when book is not found")
  void shouldGetBookByIdNotFound() {
    when(bookCacheService.getBookById(1L)).thenReturn(Optional.empty());
    when(repository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(BookNotFoundException.class, () -> bookService.getBookById(1L));
  }

  @Test
  @DisplayName("Should return recently viewed books from cache")
  void shouldGetRecentlyViewed() {
    when(bookCacheService.getRecentlyViewed()).thenReturn(List.of(book));

    List<Book> result = bookService.getRecentlyViewed();

    assertNotNull(result);
    var responseBook = result.get(0);
    assertEquals(book.getAuthor(), responseBook.getAuthor());
    assertEquals(book.getId(), responseBook.getId());
    assertEquals(book.getTitle(), responseBook.getTitle());
    assertEquals(book.getGenre(), responseBook.getGenre());
    assertEquals(book.getDescription(), responseBook.getDescription());
    assertEquals(1, result.size());
  }

  @Test
  @DisplayName("Should return books by author from cache when available")
  void shouldGetBooksByAuthorCacheHit() {
    int page = 0, size = 10;
    String author = "Jorge";
    Page<Book> expectedPage = new PageImpl<>(List.of(book));
    when(bookCacheService.getBooksByAuthor(author, page, size))
        .thenReturn(Optional.of(new CustomPage<>(expectedPage)));

    CustomPage<Book> result = bookService.getBooksByAuthor(author, page, size);

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());

    var responseBook = result.getContent().get(0);
    assertEquals(book.getAuthor(), responseBook.getAuthor());
    assertEquals(book.getId(), responseBook.getId());
    assertEquals(book.getTitle(), responseBook.getTitle());
    assertEquals(book.getGenre(), responseBook.getGenre());
    assertEquals(book.getDescription(), responseBook.getDescription());

    verify(repository, never()).findAll(any(Pageable.class));
  }

  @Test
  @DisplayName("Should fetch books by author from repository when cache is empty")
  void shouldGetBooksByAuthorCacheMiss() {
    int page = 0, size = 10;
    String author = "Jorge";

    Page<Book> expectedPage = new PageImpl<>(List.of(book));
    when(bookCacheService.getBooksByAuthor(author, page, size)).thenReturn(Optional.empty());
    when(repository.findByAuthorIgnoreCase(anyString(), any(Pageable.class)))
        .thenReturn(expectedPage);

    CustomPage<Book> result = bookService.getBooksByAuthor(author, page, size);

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());

    var responseBook = result.getContent().get(0);
    assertEquals(book.getAuthor(), responseBook.getAuthor());
    assertEquals(book.getId(), responseBook.getId());
    assertEquals(book.getTitle(), responseBook.getTitle());
    assertEquals(book.getGenre(), responseBook.getGenre());
    assertEquals(book.getDescription(), responseBook.getDescription());

    verify(bookCacheService).putCache(any(CustomPage.class), anyString());
  }

  @Test
  @DisplayName("Should return books by genre from cache when available")
  void shouldGetBooksByGenreCacheHit() {
    int page = 0, size = 10;
    String genre = "Adventure";
    Page<Book> expectedPage = new PageImpl<>(List.of(book));
    when(bookCacheService.getBooksByGenre(genre, page, size))
        .thenReturn(Optional.of(new CustomPage<>(expectedPage)));

    CustomPage<Book> result = bookService.getBooksByGenre(genre, page, size);

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());

    var responseBook = result.getContent().get(0);
    assertEquals(book.getAuthor(), responseBook.getAuthor());
    assertEquals(book.getId(), responseBook.getId());
    assertEquals(book.getTitle(), responseBook.getTitle());
    assertEquals(book.getGenre(), responseBook.getGenre());
    assertEquals(book.getDescription(), responseBook.getDescription());

    verify(repository, never()).findAll(any(Pageable.class));
  }

  @Test
  @DisplayName("Should fetch books by genre from repository when cache is empty")
  void shouldGetBooksByGenreCacheMiss() {
    int page = 0, size = 10;
    String genre = "Adventure";

    Page<Book> expectedPage = new PageImpl<>(List.of(book));
    when(bookCacheService.getBooksByGenre(genre, page, size)).thenReturn(Optional.empty());
    when(repository.findByGenreIgnoreCase(anyString(), any(Pageable.class)))
        .thenReturn(expectedPage);

    CustomPage<Book> result = bookService.getBooksByGenre(genre, page, size);

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());

    var responseBook = result.getContent().get(0);
    assertEquals(book.getAuthor(), responseBook.getAuthor());
    assertEquals(book.getId(), responseBook.getId());
    assertEquals(book.getTitle(), responseBook.getTitle());
    assertEquals(book.getGenre(), responseBook.getGenre());
    assertEquals(book.getDescription(), responseBook.getDescription());

    verify(bookCacheService).putCache(any(CustomPage.class), anyString());
  }
}
