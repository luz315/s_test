package com.spartatest.common.exception;

import com.spartatest.common.response.ApiResult;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

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

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResult<Void>> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
    String errorMessage = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
    log.warn("[{}] 입력값 검증 실패: {}", request.getRequestURI(), errorMessage);
    return ResponseEntity.badRequest()
            .body(ApiResult.error(400, errorMessage));
  }
}