package com.br.bookdata.api.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.br.bookdata.api.dtos.BookBasicDTO;
import com.br.bookdata.api.dtos.BookDTO;
import com.br.bookdata.api.exception.ResourceExceptionHandler;
import com.br.bookdata.api.facade.BookFacade;
import com.br.bookdata.domain.exception.BookNotFoundException;
import com.br.bookdata.domain.utils.CustomPage;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@DisplayName("BookControllerTest API Tests")
@ExtendWith(MockitoExtension.class)
class BookControllerTest {

  @Mock private BookFacade bookFacade;

  @InjectMocks private BookController bookController;

  private MockMvc mockMvc;

  private BookBasicDTO bookDTO;

  private CustomPage<BookBasicDTO> page;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(bookController)
            .setControllerAdvice(new ResourceExceptionHandler())
            .build();
    bookDTO = new BookBasicDTO(1L, "Book title", "Paulo", "Adventure");
    page = new CustomPage<>();
    page.setContent(List.of(bookDTO));
  }

  @Test
  @DisplayName("Test to retrieve all books successfully")
  void testGetAllBooks() throws Exception {
    when(bookFacade.getAllBooks(anyInt(), anyInt())).thenReturn(page);

    mockMvc
        .perform(get("/books"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(1))
        .andExpect(jsonPath("$.content[0].title").value(bookDTO.title()));
  }

  @Test
  @DisplayName("Test to retrieve all books when no books are available")
  void testGetAllBooksIsEmpty() throws Exception {
    when(bookFacade.getAllBooks(anyInt(), anyInt())).thenReturn(new CustomPage<>());

    mockMvc.perform(get("/books")).andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("Test to retrieve book by ID successfully")
  void testGetBookById() throws Exception {
    var book = new BookDTO(1L, "Book title", "Paulo", "Adventure", "Description");
    when(bookFacade.getBookById(1L)).thenReturn(book);

    mockMvc
        .perform(get("/books/{id}", 1L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.title").value(book.title()))
        .andExpect(jsonPath("$.author").value(book.author()))
        .andExpect(jsonPath("$.genre").value(book.genre()))
        .andExpect(jsonPath("$.description").value(book.description()));
  }

  @Test
  @DisplayName("Test to retrieve books by genre successfully")
  void testGetBooksByGenre() throws Exception {
    when(bookFacade.getBooksByGenre(anyString(), anyInt(), anyInt())).thenReturn(page);

    mockMvc
        .perform(get("/books/genre/{genre}", "Adventure").param("page", "0").param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(1))
        .andExpect(jsonPath("$.content[0].title").value(bookDTO.title()))
        .andExpect(jsonPath("$.content[0].genre").value(bookDTO.genre()))
        .andExpect(jsonPath("$.content[0].author").value(bookDTO.author()));
  }

  @Test
  @DisplayName("Test to retrieve books by author successfully")
  void testGetBooksByAuthor() throws Exception {
    when(bookFacade.getBooksByAuthor(anyString(), anyInt(), anyInt())).thenReturn(page);

    mockMvc
        .perform(get("/books/author/{author}", "Paulo").param("page", "0").param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(1))
        .andExpect(jsonPath("$.content[0].title").value(bookDTO.title()))
        .andExpect(jsonPath("$.content[0].genre").value(bookDTO.genre()))
        .andExpect(jsonPath("$.content[0].author").value(bookDTO.author()));
  }

  @Test
  @DisplayName("Test to retrieve recently viewed books when empty")
  void testGetRecentlyViewedIsEmpty() throws Exception {
    when(bookFacade.getRecentlyViewed()).thenReturn(Collections.emptyList());

    mockMvc.perform(get("/books/recently-viewed")).andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("Test to retrieve recently viewed books successfully")
  void testGetRecentlyViewed() throws Exception {
    var book = new BookDTO(1L, "Book title", "Paulo", "Adventure", "Description");
    var books = List.of(book);
    when(bookFacade.getRecentlyViewed()).thenReturn(books);

    mockMvc
        .perform(get("/books/recently-viewed"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.[0].id").value(1))
        .andExpect(jsonPath("$.[0].title").value(books.get(0).title()))
        .andExpect(jsonPath("$.[0].genre").value(books.get(0).genre()))
        .andExpect(jsonPath("$.[0].author").value(books.get(0).author()))
        .andExpect(jsonPath("$.[0].description").value(books.get(0).description()));
  }

  @Test
  @DisplayName("Test to handle BookNotFoundException")
  void testBookNotFoundException() throws Exception {
    var id = 999L;
    var msg = String.format("Book id %s not found", id);
    when(bookFacade.getBookById(999L)).thenThrow(new BookNotFoundException(msg));

    mockMvc
        .perform(get("/books/{id}", 999L))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.msg").value(msg))
        .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()));
  }

  @Test
  @DisplayName("Test to handle MethodArgumentNotValidException")
  void testMethodArgumentNotValidException() throws Exception {
    var msg =
        String.format("Field : '%s' format invalid, format required :%s", "id", "java.lang.Long");
    mockMvc
        .perform(get("/books/{id}", "invalid"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.msg").value(msg))
        .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
  }

  @Test
  @DisplayName("Test to handle general Exception")
  void testGeneralException() throws Exception {
    when(bookFacade.getAllBooks(anyInt(), anyInt())).thenThrow(RuntimeException.class);

    mockMvc
        .perform(get("/books"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.status").value(HttpStatus.INTERNAL_SERVER_ERROR.value()));
  }
}
