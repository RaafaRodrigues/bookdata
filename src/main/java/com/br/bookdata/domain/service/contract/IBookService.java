package com.br.bookdata.domain.service.contract;

import com.br.bookdata.domain.utils.CustomPage;
import java.util.List;

public interface IBookService<S, ID> {
  CustomPage<S> getAllBooks(int page, int size);

  CustomPage<S> getBooksByGenre(String genre, int page, int size);

  CustomPage<S> getBooksByAuthor(String author, int page, int size);

  List<S> getRecentlyViewed();

  S getBookById(ID id);
}
