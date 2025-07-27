package com.spartatest.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {

  private final int code;
  private final HttpStatus status;

  public CustomException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.code = errorCode.getCode();
    this.status = errorCode.getStatus();
  }
}