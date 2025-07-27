package com.spartatest.common.exception.custom;

import com.spartatest.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {
    MISSING_COOKIE(2000, "요청에 쿠키가 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    MISSING_REFRESH_TOKEN(2001, "Refresh 토큰이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    EXPIRED_TOKEN(2002, "만료된 토큰입니다.", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN(2003, "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN_TYPE(2004, "잘못된 토큰 종류입니다.", HttpStatus.BAD_REQUEST),
    PASSWORD_ACCESS_NOT_ALLOWED(2005, "패스워드는 제공되지 않습니다.", HttpStatus.FORBIDDEN),
    INVALID_CREDENTIALS(2006, "아이디 또는 비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),
    MALFORMED_REQUEST_BODY(2007, "잘못된 요청 형식입니다.", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN_SIGNATURE(2007, "토큰 서명이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED);


    private final int code;
    private final String message;
    private final HttpStatus status;
}
