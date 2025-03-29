package com.br.bookdata.api.facade;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.br.bookdata.api.dtos.BookBasicDTO;
import com.br.bookdata.api.dtos.BookDTO;
import com.br.bookdata.api.dtos.mapper.BookMapper;
import com.br.bookdata.domain.model.Book;
import com.br.bookdata.domain.service.contract.IBookService;
import com.br.bookdata.domain.utils.CustomPage;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookFacadeTest {

  @Mock private IBookService<Book, Long> bookService;

  @InjectMocks private BookFacade bookFacade;

  private Book book;
  private BookDTO bookDTO;
  private BookBasicDTO bookBasicDTO;
  private CustomPage<Book> page;

  @BeforeEach
  void setUp() {
    book =
        Book.builder()
            .title("Title")
            .id(1L)
            .genre("Fiction")
            .author("Test Author")
            .description("Description")
            .build();
    bookDTO = BookMapper.toDTO(book);
    bookBasicDTO = BookMapper.toBasicDTO(book);
    page = new CustomPage<>();
    page.setContent(List.of(book));
  }

  @Test
  void shouldGetAllBooks() {
    when(bookService.getAllBooks(0, 10)).thenReturn(page);

    CustomPage<BookBasicDTO> result = bookFacade.getAllBooks(0, 10);

    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    assertEquals(bookBasicDTO, result.getContent().get(0));

    verify(bookService, times(1)).getAllBooks(0, 10);
  }

  @Test
  void shouldGetBooksByGenre() {
    when(bookService.getBooksByGenre("Genre", 0, 10)).thenReturn(page);

    CustomPage<BookBasicDTO> result = bookFacade.getBooksByGenre("Genre", 0, 10);

    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    assertEquals(bookBasicDTO, result.getContent().get(0));

    verify(bookService, times(1)).getBooksByGenre("Genre", 0, 10);
  }

  @Test
  void shouldGetBooksByAuthor() {
    when(bookService.getBooksByAuthor("Author", 0, 10)).thenReturn(page);

    CustomPage<BookBasicDTO> result = bookFacade.getBooksByAuthor("Author", 0, 10);

    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    assertEquals(bookBasicDTO, result.getContent().get(0));

    verify(bookService, times(1)).getBooksByAuthor("Author", 0, 10);
  }

  @Test
  void shouldGetRecentlyViewed() {
    List<Book> recentlyViewedBooks = Collections.singletonList(book);

    when(bookService.getRecentlyViewed()).thenReturn(recentlyViewedBooks);

    List<BookDTO> result = bookFacade.getRecentlyViewed();

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(bookDTO, result.get(0));

    verify(bookService, times(1)).getRecentlyViewed();
  }

  @Test
  void shouldGetBookById() {
    when(bookService.getBookById(1L)).thenReturn(book);

    BookDTO result = bookFacade.getBookById(1L);

    assertNotNull(result);
    assertEquals(bookDTO, result);

    verify(bookService, times(1)).getBookById(1L);
  }
}
