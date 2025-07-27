package com.spartatest.common.exception.custom;

import com.spartatest.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    USER_ALREADY_EXISTS(1001, "이미 가입된 사용자입니다.", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1002, "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_CREDENTIALS(1003, "아이디 또는 비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED(1004, "관리자 권한이 필요한 요청입니다.", HttpStatus.FORBIDDEN),
    USERNAME_NOT_FOUND(1005, "해당 사용자 이름이 존재하지 않습니다.", HttpStatus.NOT_FOUND);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
