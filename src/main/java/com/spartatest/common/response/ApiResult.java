package com.spartatest.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class ApiResult<T> {

    private final boolean success;
    private final int code;
    private final String message;
    private final T data;

    public static <T> ApiResult<T> success(T data, String message) {
        return new ApiResult<>(true, HttpStatus.OK.value(), message, data);
    }

    public static <T> ApiResult<T> success(T data) {
        return success(data, "요청이 성공했습니다.");
    }

    public static ApiResult<Void> success(String message) {
        return new ApiResult<>(true, HttpStatus.OK.value(), message, null);
    }

    public static <T> ApiResult<T> error(HttpStatus status, String message) {
        return new ApiResult<>(false, status.value(), message, null);
    }

    public static <T> ApiResult<T> error(int code, String message) {
        return new ApiResult<>(false, code, message, null);
    }
}
