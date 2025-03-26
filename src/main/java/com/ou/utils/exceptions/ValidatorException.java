package com.ou.utils.exceptions;

public class ValidatorException extends RuntimeException {
  private String field;

  public ValidatorException(String message, String field) {
    super(message);
  }

  public String getFieldName() {
    return field;
  }
}
