package com.br.bookdata.domain.service.contract;

import com.br.bookdata.domain.utils.CustomPage;
import java.util.List;
import java.util.Optional;

public interface IBookCacheService<S, ID> {
  Optional<CustomPage<S>> getAllBooks(int page, int size);

  Optional<CustomPage<S>> getBooksByGenre(String genre, int page, int size);

  Optional<CustomPage<S>> getBooksByAuthor(String author, int page, int size);

  Optional<S> getBookById(ID id);

  List<S> updateRecentlyViewed(S object);

  List<S> getRecentlyViewed();

  void putCache(S object, String cacheKey);

  void putCache(CustomPage<S> object, String cacheKey);
}
