package com.br.bookdata.domain.observer;

import static org.mockito.Mockito.*;

import com.br.bookdata.domain.model.Book;
import com.br.bookdata.domain.observer.contract.IObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookViewedISubject Test")
class BookViewedISubjectTest {

  private BookViewedISubject bookViewedISubject;

  @Mock private ApplicationContext applicationContext;

  @Mock private BookRecentlyViewedIObserver bookRecentlyViewedIObserver;

  @Mock private IObserver<Book> anotherObserver;

  @Mock private Book book;

  @BeforeEach
  void setUp() {
    when(applicationContext.getBean(
            "bookRecentlyViewedIObserver", BookRecentlyViewedIObserver.class))
        .thenReturn(bookRecentlyViewedIObserver);
    bookViewedISubject = new BookViewedISubject(applicationContext);
  }

  @Test
  @DisplayName("Should notify observers when book is updated")
  void shouldNotifyObserversWhenBookIsUpdated() {
    bookViewedISubject.notifyObservers(book);
    verify(bookRecentlyViewedIObserver, times(1)).update(book);
  }

  @Test
  @DisplayName("Should add observer and notify all")
  void shouldAddObserverAndNotifyAll() {
    bookViewedISubject.addObserver(anotherObserver);
    bookViewedISubject.notifyObservers(book);
    verify(bookRecentlyViewedIObserver, times(1)).update(book);
    verify(anotherObserver, times(1)).update(book);
  }

  @Test
  @DisplayName("Should remove observer and not notify it")
  void shouldRemoveObserverAndNotNotifyIt() {
    bookViewedISubject.addObserver(anotherObserver);
    bookViewedISubject.removeObserver(anotherObserver);
    bookViewedISubject.notifyObservers(book);
    verify(bookRecentlyViewedIObserver, times(1)).update(book);
    verify(anotherObserver, never()).update(book);
  }
}
