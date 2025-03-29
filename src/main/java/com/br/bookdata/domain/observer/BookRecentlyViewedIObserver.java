package com.br.bookdata.domain.observer;

import com.br.bookdata.domain.model.Book;
import com.br.bookdata.domain.observer.contract.IObserver;
import com.br.bookdata.domain.service.contract.IBookCacheService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Log4j2
@Component("bookRecentlyViewedIObserver")
public class BookRecentlyViewedIObserver implements IObserver<Book> {

  private final IBookCacheService<Book, Long> bookCacheService;

  public BookRecentlyViewedIObserver(
      @Qualifier("bookCacheServiceImpl") IBookCacheService<Book, Long> bookCacheService) {
    this.bookCacheService = bookCacheService;
  }

  @Override
  public void update(Book book) {
    log.info("Received update request for book: {}", book.getId());

    try {
      bookCacheService.updateRecentlyViewed(book);
      log.info("Successfully updated recently viewed cache for book: {}", book.getId());
    } catch (Exception e) {
      log.error(
          "Failed to update recently viewed cache for book: {}. Error: {}",
          book.getId(),
          e.getMessage(),
          e);
    }
  }
}
