package com.br.bookdata.domain.observer;

import com.br.bookdata.domain.model.Book;
import com.br.bookdata.domain.observer.contract.IObserver;
import com.br.bookdata.domain.observer.contract.ISubject;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component("bookViewedISubject")
public class BookViewedISubject implements ISubject<Book> {
  private final List<IObserver<Book>> iObservers = new ArrayList<>();

  public BookViewedISubject(ApplicationContext applicationContext) {
    this.iObservers.add(
        applicationContext.getBean(
            "bookRecentlyViewedIObserver", BookRecentlyViewedIObserver.class));
  }

  public void addObserver(IObserver<Book> observer) {
    iObservers.add(observer);
  }

  public void removeObserver(IObserver<Book> observer) {
    iObservers.remove(observer);
  }

  public void notifyObservers(Book book) {
    for (IObserver<Book> observer : iObservers) {
      observer.update(book);
    }
  }
}
