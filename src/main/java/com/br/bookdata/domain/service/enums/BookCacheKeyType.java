package com.br.bookdata.domain.service.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BookCacheKeyType {
  BOOK_ID_KEY("book-id-"),
  BOOK_PAGED_KEY("books-page-size-"),
  BOOK_PAGED_GENRE_KEY("books-page-size-genre-"),
  BOOK_PAGED_AUTHOR_KEY("books-page-size-genre-author-"),
  RECENTLY_VIEWED_KEY("recentlyViewedBooks");

  private final String value;
}
