package com.br.bookdata.domain.observer;

import static org.mockito.Mockito.*;

import com.br.bookdata.domain.model.Book;
import com.br.bookdata.domain.service.contract.IBookCacheService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookRecentlyViewedIObserverTest {

  @Mock private IBookCacheService<Book, Long> bookCacheService;

  @InjectMocks private BookRecentlyViewedIObserver observer;

  private Book book;

  @BeforeEach
  void setUp() {
    book =
        Book.builder()
            .id(1L)
            .title("Test Book")
            .author("Author")
            .genre("Fiction")
            .description("Test Description")
            .build();
  }

  @Test
  @DisplayName("Should update recently viewed books successfully")
  void shouldUpdateRecentlyViewedBooksSuccessfully() {
    when(bookCacheService.updateRecentlyViewed(any(Book.class))).thenReturn(List.of(book));

    observer.update(book);

    verify(bookCacheService, times(1)).updateRecentlyViewed(book);
  }

  @Test
  @DisplayName("Should handle exception when updating recently viewed books")
  void shouldHandleExceptionWhenUpdatingRecentlyViewedBooks() {
    doThrow(new RuntimeException("Cache update failed"))
        .when(bookCacheService)
        .updateRecentlyViewed(book);

    observer.update(book);

    verify(bookCacheService, times(1)).updateRecentlyViewed(book);
  }
}
