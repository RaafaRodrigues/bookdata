package com.br.bookdata.api.exception;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.br.bookdata.domain.exception.BookNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ExtendWith(MockitoExtension.class)
class ResourceExceptionHandlerTest {

  private ResourceExceptionHandler exceptionHandler;

  @BeforeEach
  void setUp() {
    exceptionHandler = new ResourceExceptionHandler();
  }

  @Test
  @DisplayName("Should handle MethodArgumentNotValidException and return 400 Bad Request")
  void testValidationException() {
    MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
    when(exception.getFieldErrors())
        .thenReturn(List.of(new FieldError("object", "field", "must not be blank")));

    ResponseEntity<StandardError> response = exceptionHandler.validation(exception);

    assertNotNull(response.getBody());
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    assertEquals("Validation error", response.getBody().getMsg());
  }

  @Test
  @DisplayName("Should handle MethodArgumentTypeMismatchException and return 400 Bad Request")
  void testMethodArgumentTypeMismatchException() {
    MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);

    when(exception.getName()).thenReturn("page");

    Class requiredType = Integer.TYPE;

    when(exception.getRequiredType()).thenReturn(requiredType);

    MethodParameter mockMethodParameter = mock(MethodParameter.class);
    when(exception.getParameter()).thenReturn(mockMethodParameter);

    Class parameterType = Integer.TYPE;
    when(mockMethodParameter.getParameterType()).thenReturn(parameterType);

    ResponseEntity<StandardError> response = exceptionHandler.methodArgumentTypeMismatch(exception);

    assertNotNull(response.getBody());
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    assertTrue(response.getBody().getMsg().contains("Field : 'page' format invalid"));
  }

  @Test
  @DisplayName("Should handle HandlerMethodValidationException and return 400 Bad Request")
  void testHandlerMethodValidationException() {
    HandlerMethodValidationException exception = mock(HandlerMethodValidationException.class);
    when(exception.getMessage()).thenReturn("Invalid method argument");

    ResponseEntity<StandardError> response = exceptionHandler.handlerMethodValidation(exception);

    assertNotNull(response.getBody());
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    assertEquals("Invalid method argument", response.getBody().getMsg());
  }

  @Test
  @DisplayName(
      "Should handle ConstraintViolationException and return 400 Bad Request and multiples violations")
  void testConstraintViolationMultiplesException() {
    ConstraintViolationException exception = mock(ConstraintViolationException.class);
    ConstraintViolation<?> violation = mock(ConstraintViolation.class);
    ConstraintViolation<?> violation2 = mock(ConstraintViolation.class);
    Path mockPath = mock(Path.class);

    when(mockPath.toString()).thenReturn("object.size").thenReturn("object.page");
    when(violation.getPropertyPath()).thenReturn(mockPath);
    when(violation.getMessage()).thenReturn("must not be blank");

    when(violation2.getPropertyPath()).thenReturn(mockPath);
    when(violation2.getMessage()).thenReturn("must not be blank");

    Set<ConstraintViolation<?>> violations = Set.of(violation, violation2);
    when(exception.getConstraintViolations()).thenReturn(violations);

    ResponseEntity<Object> response = exceptionHandler.constraintViolation(exception);

    assertNotNull(response.getBody());
    assertInstanceOf(ValidationError.class, response.getBody());

    ValidationError validationError = (ValidationError) response.getBody();
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    assertTrue(validationError.getErrors().size() > 1);
  }

  @Test
  @DisplayName("Should handle ConstraintViolationException and return 400 Bad Request")
  void testConstraintViolationException() {
    ConstraintViolationException exception = mock(ConstraintViolationException.class);
    ConstraintViolation<?> violation = mock(ConstraintViolation.class);

    when(violation.getMessage()).thenReturn("must not be blank");

    Set<ConstraintViolation<?>> violations = Set.of(violation);
    when(exception.getConstraintViolations()).thenReturn(violations);

    ResponseEntity<Object> response = exceptionHandler.constraintViolation(exception);

    assertNotNull(response.getBody());
    assertInstanceOf(StandardError.class, response.getBody());

    StandardError standardError = (StandardError) response.getBody();
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    assertEquals("must not be blank", standardError.getMsg());
  }

  @Test
  @DisplayName("Should handle BookNotFoundException and return 404 Not Found")
  void testBookNotFoundException() {
    BookNotFoundException exception = new BookNotFoundException("Book not found");

    ResponseEntity<StandardError> response = exceptionHandler.bookNotFound(exception);

    assertNotNull(response.getBody());
    assertInstanceOf(StandardError.class, response.getBody());

    var error = response.getBody();
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode().value());
    assertEquals("Book not found", error.getMsg());
  }

  @Test
  @DisplayName("Should handle generic Exception and return 500 Internal Server Error")
  void testGenericException() {
    Exception exception = new Exception("Unexpected error");

    ResponseEntity<StandardError> response = exceptionHandler.exception(exception);

    assertNotNull(response.getBody());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatusCode().value());
    assertEquals("Unexpected error", response.getBody().getMsg());
  }
}
