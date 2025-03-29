package com.br.bookdata.api.controller;

import com.br.bookdata.api.dtos.BookBasicDTO;
import com.br.bookdata.api.dtos.BookDTO;
import com.br.bookdata.api.facade.BookFacade;
import com.br.bookdata.domain.utils.CustomPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Books API", description = "Endpoints for searching books")
@RestController
@RequestMapping("/books")
@Validated
public class BookController {

  private final BookFacade bookFacade;

  public BookController(BookFacade bookFacade) {
    this.bookFacade = bookFacade;
  }

  @Operation(
      summary = "Get all books",
      description = "Retrieve a paginated list of all books",
      responses = {@ApiResponse(description = "List of books", responseCode = "200")})
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CustomPage<BookBasicDTO>> getAllBooks(
      @RequestParam(name = "page", defaultValue = "0")
          @Min(value = 0, message = "Page number must be at least 0")
          Integer page,
      @RequestParam(name = "size", defaultValue = "10")
          @Min(value = 1, message = "Size must be at least 1")
          @Max(value = 100, message = "Size must not exceed 100")
          Integer size) {
    CustomPage<BookBasicDTO> books = bookFacade.getAllBooks(page, size);
    if (books.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok().body(books);
  }

  @Operation(
      summary = "Get a book by ID",
      description = "Retrieve a specific book by its ID",
      responses = {
        @ApiResponse(description = "Book details", responseCode = "200"),
        @ApiResponse(description = "Book not found", responseCode = "404")
      })
  @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BookDTO> getBookById(
      @Parameter(description = "ID of the book to retrieve") @PathVariable Long id) {
    return ResponseEntity.ok().body(bookFacade.getBookById(id));
  }

  @Operation(
      summary = "Get books by genre",
      description = "Retrieve a paginated list of books filtered by genre",
      responses = {@ApiResponse(description = "List of books by genre", responseCode = "200")})
  @GetMapping(path = "/genre/{genre}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CustomPage<BookBasicDTO>> getBooksByGenre(
      @Parameter(description = "Genre to filter books by") @PathVariable @NotNull String genre,
      @RequestParam(name = "page", defaultValue = "0")
          @Min(value = 0, message = "Page number must be at least 0")
          Integer page,
      @RequestParam(name = "size", defaultValue = "10")
          @Min(value = 1, message = "Size must be at least 1")
          @Max(value = 100, message = "Size must not exceed 100")
          Integer size) {
    CustomPage<BookBasicDTO> books = bookFacade.getBooksByGenre(genre, page, size);
    if (books.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(books);
  }

  @Operation(
      summary = "Get books by author",
      description = "Retrieve a paginated list of books filtered by author",
      responses = {@ApiResponse(description = "List of books by author", responseCode = "200")})
  @GetMapping(path = "/author/{author}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CustomPage<BookBasicDTO>> getBooksByAuthor(
      @Parameter(description = "Author to filter books by") @PathVariable @NotNull String author,
      @RequestParam(name = "page", defaultValue = "0")
          @Min(value = 0, message = "Page number must be at least 0")
          Integer page,
      @RequestParam(name = "size", defaultValue = "10")
          @Min(value = 1, message = "Size must be at least 1")
          @Max(value = 100, message = "Size must not exceed 100")
          Integer size) {
    CustomPage<BookBasicDTO> books = bookFacade.getBooksByAuthor(author, page, size);
    if (books.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(books);
  }

  @Operation(
      summary = "Get recently viewed books",
      description = "Retrieve a list of the last 10 recently viewed books",
      responses = {
        @ApiResponse(description = "List of recently viewed books", responseCode = "200")
      })
  @GetMapping(path = "/recently-viewed", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<BookDTO>> getRecentlyViewed() {
    List<BookDTO> books = bookFacade.getRecentlyViewed();
    if (books.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok().body(books);
  }
}
