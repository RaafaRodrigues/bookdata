package com.br.bookdata.domain.service;

import static com.br.bookdata.domain.service.enums.BookCacheKeyType.*;

import com.br.bookdata.domain.cache.contract.ICache;
import com.br.bookdata.domain.model.Book;
import com.br.bookdata.domain.service.contract.IBookCacheService;
import com.br.bookdata.domain.service.enums.BookCacheKeyType;
import com.br.bookdata.domain.utils.CustomPage;
import com.fasterxml.jackson.core.type.TypeReference;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("bookCacheServiceImpl")
@Log4j2
public class BookCacheServiceImpl implements IBookCacheService<Book, Long> {
  private static final String CACHE_NAME = "3a1c7646-c96c-424f-b90f-10181e536ff2-books";
  private static final Duration DEFAULT_CACHE_DURATION = Duration.ofHours(1);
  private static final Duration SHORT_CACHE_DURATION = Duration.ofMinutes(10);
  private final ICache cache;

  public BookCacheServiceImpl(@Qualifier("redisCacheImpl") ICache cache) {
    this.cache = cache;
  }

  @Override
  public Optional<CustomPage<Book>> getAllBooks(int page, int size) {
    String cacheKey = mountKeyByPaged(BOOK_PAGED_KEY, page, size, "");
    return cache.getFromCache(cacheKey, new TypeReference<CustomPage<Book>>() {}, CACHE_NAME);
  }

  @Override
  public Optional<CustomPage<Book>> getBooksByGenre(String genre, int page, int size) {
    String cacheKey = mountKeyByPaged(BOOK_PAGED_GENRE_KEY, page, size, genre);
    return cache.getFromCache(cacheKey, new TypeReference<CustomPage<Book>>() {}, CACHE_NAME);
  }

  @Override
  public Optional<CustomPage<Book>> getBooksByAuthor(String author, int page, int size) {
    String cacheKey = mountKeyByPaged(BOOK_PAGED_AUTHOR_KEY, page, size, author);
    return cache.getFromCache(cacheKey, new TypeReference<CustomPage<Book>>() {}, CACHE_NAME);
  }

  @Override
  public Optional<Book> getBookById(Long id) {
    String cacheKey = mountKeyById(BOOK_ID_KEY, id);
    return cache.getFromCache(cacheKey, new TypeReference<Book>() {}, CACHE_NAME);
  }

  @Override
  public List<Book> updateRecentlyViewed(Book book) {
    String cacheKey = BookCacheKeyType.RECENTLY_VIEWED_KEY.getValue();

    List<Book> cachedBooks =
        cache
            .getFromCache(cacheKey, new TypeReference<List<Book>>() {}, CACHE_NAME)
            .orElse(new ArrayList<>());

    cachedBooks.removeIf(b -> b.getId().equals(book.getId()));
    cachedBooks.add(0, book);

    if (cachedBooks.size() > 10) {
      cachedBooks.remove(cachedBooks.size() - 1);
    }

    cache.putToCache(cacheKey, cachedBooks, CACHE_NAME, null);

    return cachedBooks;
  }

  @Override
  public List<Book> getRecentlyViewed() {
    String cacheKey = BookCacheKeyType.RECENTLY_VIEWED_KEY.getValue();
    return cache
        .getFromCache(cacheKey, new TypeReference<List<Book>>() {}, CACHE_NAME)
        .orElse(new ArrayList<>());
  }

  @Override
  public void putCache(Book book, String cacheKey) {
    cache.putToCache(cacheKey, book, CACHE_NAME, DEFAULT_CACHE_DURATION);
  }

  @Override
  public void putCache(CustomPage<Book> books, String cacheKey) {
    cache.putToCache(cacheKey, books, CACHE_NAME, SHORT_CACHE_DURATION);
  }

  public static String mountKeyByPaged(
      BookCacheKeyType key, int page, int size, String optionalFilter) {
    return Optional.ofNullable(optionalFilter)
        .filter(filter -> !filter.isBlank())
        .map(String::toUpperCase)
        .map(filter -> key.getValue() + page + "-" + size + "-" + filter)
        .orElse(key.getValue() + page + "-" + size);
  }

  public static String mountKeyByPaged(BookCacheKeyType key, int page, int size) {
    return mountKeyByPaged(key, page, size, "");
  }

  public static String mountKeyById(BookCacheKeyType key, Object id) {
    return key.getValue() + id;
  }
}
