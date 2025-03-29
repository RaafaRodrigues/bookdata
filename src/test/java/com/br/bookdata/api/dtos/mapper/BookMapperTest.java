package com.br.bookdata.api.dtos.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.br.bookdata.api.dtos.BookBasicDTO;
import com.br.bookdata.api.dtos.BookDTO;
import com.br.bookdata.domain.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BookMapperTest {

  private Book book;

  @BeforeEach
  void setUp() {
    book = new Book(1L, "Book Title", "Paulo", "Adventure", "Description");
  }

  @Test
  void testToDTO() {
    BookDTO bookDTO = BookMapper.toDTO(book);

    assertEquals(book.getId(), bookDTO.id());
    assertEquals(book.getTitle(), bookDTO.title());
    assertEquals(book.getAuthor(), bookDTO.author());
    assertEquals(book.getGenre(), bookDTO.genre());
    assertEquals(book.getDescription(), bookDTO.description());
  }

  @Test
  void testToBasicDTO() {
    BookBasicDTO bookDTO = BookMapper.toBasicDTO(book);

    assertEquals(book.getId(), bookDTO.id());
    assertEquals(book.getTitle(), bookDTO.title());
    assertEquals(book.getAuthor(), bookDTO.author());
    assertEquals(book.getGenre(), bookDTO.genre());
  }
}
