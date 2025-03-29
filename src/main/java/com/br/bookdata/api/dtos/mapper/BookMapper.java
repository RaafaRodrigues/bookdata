package com.br.bookdata.api.dtos.mapper;

import com.br.bookdata.api.dtos.BookBasicDTO;
import com.br.bookdata.api.dtos.BookDTO;
import com.br.bookdata.domain.model.Book;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BookMapper {

  public static BookDTO toDTO(Book book) {
    return new BookDTO(
        book.getId(), book.getTitle(), book.getAuthor(), book.getGenre(), book.getDescription());
  }

  public static BookBasicDTO toBasicDTO(Book book) {
    return new BookBasicDTO(book.getId(), book.getTitle(), book.getAuthor(), book.getGenre());
  }
}
