package com.br.bookdata.domain.service;

import static com.br.bookdata.domain.service.BookCacheServiceImpl.mountKeyById;
import static com.br.bookdata.domain.service.enums.BookCacheKeyType.BOOK_ID_KEY;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.br.bookdata.domain.cache.contract.ICache;
import com.br.bookdata.domain.model.Book;
import com.br.bookdata.domain.utils.CustomPage;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;

@ExtendWith(MockitoExtension.class)
class BookCacheServiceImplTest {

  @Mock private ICache cache;

  @InjectMocks private BookCacheServiceImpl bookCacheService;

  private Book book;

  @BeforeEach
  void setUp() {
    book =
        Book.builder()
            .id(1L)
            .title("Test Book")
            .author("Test Author")
            .genre("Fiction")
            .description("A test book description")
            .build();
  }

  @Test
  @DisplayName("Should retrieve all books from cache when available")
  void shouldGetAllBooksFromCache() {
    int page = 0, size = 10;
    CustomPage<Book> cachedPage = new CustomPage<>(new PageImpl<>(List.of(book)));
    when(cache.getFromCache(anyString(), any(TypeReference.class), anyString()))
        .thenReturn(Optional.of(cachedPage));

    Optional<CustomPage<Book>> result = bookCacheService.getAllBooks(page, size);

    assertTrue(result.isPresent());
    assertEquals(1, result.get().getContent().size());

    var responseBook = result.get().getContent().get(0);
    assertEquals(book.getAuthor(), responseBook.getAuthor());
    assertEquals(book.getId(), responseBook.getId());
    assertEquals(book.getTitle(), responseBook.getTitle());
    assertEquals(book.getGenre(), responseBook.getGenre());
    assertEquals(book.getDescription(), responseBook.getDescription());
  }

  @Test
  @DisplayName("Should return empty when books are not in cache")
  void shouldReturnEmptyWhenCacheMiss() {
    int page = 0, size = 10;
    when(cache.getFromCache(anyString(), any(TypeReference.class), anyString()))
        .thenReturn(Optional.empty());

    Optional<CustomPage<Book>> result = bookCacheService.getAllBooks(page, size);

    assertFalse(result.isPresent());
  }

  @Test
  @DisplayName("Should retrieve book by ID from cache")
  void shouldGetBookByIdFromCache() {
    when(cache.getFromCache(anyString(), any(TypeReference.class), anyString()))
        .thenReturn(Optional.of(book));

    Optional<Book> result = bookCacheService.getBookById(1L);

    assertTrue(result.isPresent());
    assertEquals(book.getId(), result.get().getId());
    assertEquals(book.getAuthor(), result.get().getAuthor());
    assertEquals(book.getTitle(), result.get().getTitle());
    assertEquals(book.getGenre(), result.get().getGenre());
    assertEquals(book.getDescription(), result.get().getDescription());
  }

  @Test
  @DisplayName("Should return empty when book ID is not in cache")
  void shouldReturnEmptyWhenBookIdCacheMiss() {
    when(cache.getFromCache(anyString(), any(TypeReference.class), anyString()))
        .thenReturn(Optional.empty());

    Optional<Book> result = bookCacheService.getBookById(1L);

    assertFalse(result.isPresent());
  }

  @Test
  @DisplayName("Should update recently viewed books in cache")
  void shouldUpdateRecentlyViewedBooks() {
    when(cache.getFromCache(anyString(), any(TypeReference.class), anyString()))
        .thenReturn(Optional.empty());

    List<Book> result = bookCacheService.updateRecentlyViewed(book);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(book.getId(), result.get(0).getId());

    var responseBook = result.get(0);
    assertEquals(book.getAuthor(), responseBook.getAuthor());
    assertEquals(book.getId(), responseBook.getId());
    assertEquals(book.getTitle(), responseBook.getTitle());
    assertEquals(book.getGenre(), responseBook.getGenre());
    assertEquals(book.getDescription(), responseBook.getDescription());

    verify(cache).putToCache(anyString(), any(List.class), anyString(), isNull());
  }

  @Test
  @DisplayName("Should retrieve recently viewed books from cache")
  void shouldGetRecentlyViewedBooksFromCache() {
    when(cache.getFromCache(anyString(), any(TypeReference.class), anyString()))
        .thenReturn(Optional.of(List.of(book)));

    List<Book> result = bookCacheService.getRecentlyViewed();

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
  @DisplayName("Should return empty list when recently viewed books are not in cache")
  void shouldReturnEmptyListWhenNoRecentlyViewedBooksInCache() {
    when(cache.getFromCache(anyString(), any(TypeReference.class), anyString()))
        .thenReturn(Optional.empty());

    List<Book> result = bookCacheService.getRecentlyViewed();

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Should retrieve all books by genre from cache when available")
  void shouldGetBooksByGenreFromCache() {
    int page = 0, size = 10;
    String genre = "Fiction";
    CustomPage<Book> cachedPage = new CustomPage<>(new PageImpl<>(List.of(book)));
    when(cache.getFromCache(anyString(), any(TypeReference.class), anyString()))
        .thenReturn(Optional.of(cachedPage));

    Optional<CustomPage<Book>> result = bookCacheService.getBooksByGenre(genre, page, size);

    assertTrue(result.isPresent());
    assertEquals(1, result.get().getContent().size());

    var responseBook = result.get().getContent().get(0);
    assertEquals(book.getAuthor(), responseBook.getAuthor());
    assertEquals(book.getId(), responseBook.getId());
    assertEquals(book.getTitle(), responseBook.getTitle());
    assertEquals(book.getGenre(), responseBook.getGenre());
    assertEquals(book.getDescription(), responseBook.getDescription());
  }

  @Test
  @DisplayName("Should retrieve all books by genre return empty when cache is empty")
  void shouldGetBooksByGenreReturnEmptyWhenCacheIsEmpty() {
    int page = 0, size = 10;
    String genre = "Fiction";
    when(cache.getFromCache(anyString(), any(TypeReference.class), anyString()))
        .thenReturn(Optional.empty());

    Optional<CustomPage<Book>> result = bookCacheService.getBooksByGenre(genre, page, size);

    assertFalse(result.isPresent());
  }

  @Test
  @DisplayName("Should retrieve all books by author from cache when available")
  void shouldGetBooksByAuthorFromCache() {
    int page = 0, size = 10;
    String author = "Frederico";
    CustomPage<Book> cachedPage = new CustomPage<>(new PageImpl<>(List.of(book)));
    when(cache.getFromCache(anyString(), any(TypeReference.class), anyString()))
        .thenReturn(Optional.of(cachedPage));

    Optional<CustomPage<Book>> result = bookCacheService.getBooksByGenre(author, page, size);

    assertTrue(result.isPresent());
    assertEquals(1, result.get().getContent().size());

    var responseBook = result.get().getContent().get(0);
    assertEquals(book.getAuthor(), responseBook.getAuthor());
    assertEquals(book.getId(), responseBook.getId());
    assertEquals(book.getTitle(), responseBook.getTitle());
    assertEquals(book.getGenre(), responseBook.getGenre());
    assertEquals(book.getDescription(), responseBook.getDescription());
  }

  @Test
  @DisplayName("Should retrieve all books by author return empty when cache is empty")
  void shouldGetBooksByAuthorReturnEmptyWhenCacheIsEmpty() {
    int page = 0, size = 10;
    String author = "Frederico";
    when(cache.getFromCache(anyString(), any(TypeReference.class), anyString()))
        .thenReturn(Optional.empty());

    Optional<CustomPage<Book>> result = bookCacheService.getBooksByAuthor(author, page, size);

    assertFalse(result.isPresent());
  }

  @Test
  @DisplayName("Should put book in cache")
  void shouldPutBookInCache() {
    String cacheKey = mountKeyById(BOOK_ID_KEY, book.getId());
    bookCacheService.putCache(book, cacheKey);
    verify(cache, times(1)).putToCache(eq(cacheKey), any(), anyString(), any());
  }

  @Test
  @DisplayName("Should put list the books in cache")
  void shouldPutBooksInCache() {
    String cacheKey = mountKeyById(BOOK_ID_KEY, book.getId());
    CustomPage<Book> cachedPage = new CustomPage<>(new PageImpl<>(List.of(book)));
    bookCacheService.putCache(cachedPage, cacheKey);
    verify(cache, times(1)).putToCache(eq(cacheKey), eq(cachedPage), anyString(), any());
  }
}
