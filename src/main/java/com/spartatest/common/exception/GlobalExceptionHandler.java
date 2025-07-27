package com.spartatest.common.exception;

import com.spartatest.common.response.ApiResult;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ApiResult<Void>> handleCustomException(CustomException e, HttpServletRequest request) {
    log.error("[{}] {} - {}", request.getRequestURI(), e.getStatus(), e.getMessage());
    return ResponseEntity.status(e.getStatus())
            .body(ApiResult.error(e.getCode(), e.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResult<Void>> handleUnknownException(Exception e, HttpServletRequest request) {
    log.error("[{}] 예외 발생: {}", request.getRequestURI(), e.getMessage(), e);
    return ResponseEntity.status(500)
            .body(ApiResult.error(500, "서버 내부 오류가 발생했습니다."));
  }
}