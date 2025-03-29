package com.br.bookdata.api.exception;

import com.br.bookdata.domain.exception.BookNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.util.Optional;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public final class ResourceExceptionHandler {
  private static final String DEFAULT_MSG_VALIDATION_ERROR = "Validation error";

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<StandardError> validation(
      MethodArgumentNotValidException methodArgumentNotValidException) {
    ValidationError validationError =
        new ValidationError(
            HttpStatus.BAD_REQUEST.value(),
            DEFAULT_MSG_VALIDATION_ERROR,
            System.currentTimeMillis());
    methodArgumentNotValidException
        .getFieldErrors()
        .forEach(
            error ->
                validationError.setList(
                    new FieldMessage(error.getField(), error.getDefaultMessage())));
    return ResponseEntity.status(validationError.getStatus()).body(validationError);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<StandardError> methodArgumentTypeMismatch(
      MethodArgumentTypeMismatchException methodArgumentTypeMismatchException) {
    StandardError standardError =
        new StandardError(
            HttpStatus.BAD_REQUEST.value(),
            String.format(
                "Field : '%s' format invalid, format required :%s",
                methodArgumentTypeMismatchException.getName(),
                Optional.ofNullable(methodArgumentTypeMismatchException.getRequiredType())
                    .map(Class::getName)
                    .orElse(
                        methodArgumentTypeMismatchException
                            .getParameter()
                            .getParameterType()
                            .getName())),
            System.currentTimeMillis());

    return ResponseEntity.status(standardError.getStatus()).body(standardError);
  }

  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<StandardError> handlerMethodValidation(
      HandlerMethodValidationException handlerMethodValidationException) {
    StandardError standardError =
        new StandardError(
            HttpStatus.BAD_REQUEST.value(),
            handlerMethodValidationException.getMessage(),
            System.currentTimeMillis());

    return ResponseEntity.status(standardError.getStatus()).body(standardError);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Object> constraintViolation(
      ConstraintViolationException constraintViolationException) {

    Set<ConstraintViolation<?>> violations =
        Optional.ofNullable(constraintViolationException.getConstraintViolations())
            .orElse(Set.of());

    if (violations.size() > 1) {
      ValidationError validationError =
          new ValidationError(
              HttpStatus.BAD_REQUEST.value(),
              DEFAULT_MSG_VALIDATION_ERROR,
              System.currentTimeMillis());

      violations.forEach(
          error ->
              validationError.setList(
                  new FieldMessage(
                      Optional.ofNullable(error.getPropertyPath())
                          .map(Path::toString)
                          .map(path -> path.split("\\."))
                          .filter(path -> path.length >= 1)
                          .map(path -> path[1])
                          .orElse("unknown_field"),
                      Optional.ofNullable(error.getMessage()).orElse("Invalid value"))));

      return ResponseEntity.status(validationError.getStatus()).body(validationError);
    }

    String message =
        violations.stream()
            .findFirst()
            .map(ConstraintViolation::getMessage)
            .orElse(DEFAULT_MSG_VALIDATION_ERROR);

    StandardError standardError =
        new StandardError(HttpStatus.BAD_REQUEST.value(), message, System.currentTimeMillis());

    return ResponseEntity.status(standardError.getStatus()).body(standardError);
  }

  @ExceptionHandler(BookNotFoundException.class)
  public ResponseEntity<StandardError> bookNotFound(BookNotFoundException bookNotFoundException) {
    StandardError standardError =
        new StandardError(
            HttpStatus.NOT_FOUND.value(),
            bookNotFoundException.getMessage(),
            System.currentTimeMillis());

    return ResponseEntity.status(standardError.getStatus()).body(standardError);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<StandardError> exception(Exception exception) {
    StandardError standardError =
        new StandardError(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            exception.getMessage(),
            System.currentTimeMillis());

    return ResponseEntity.status(standardError.getStatus()).body(standardError);
  }
}
