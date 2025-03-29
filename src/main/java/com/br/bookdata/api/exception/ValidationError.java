package com.br.bookdata.api.exception;

import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ValidationError extends StandardError {

  private final List<FieldMessage> errors = new ArrayList<>();

  public ValidationError(Integer status, String msg, Long timeStamp) {
    super(status, msg, timeStamp);
  }

  public void setList(FieldMessage fieldMessage) {
    this.errors.add(fieldMessage);
  }
}
