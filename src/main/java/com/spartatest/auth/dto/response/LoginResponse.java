package com.spartatest.auth.dto.response;

public record LoginResponse(
        String token
) {
    public static LoginResponse from(String accessToken) {
        return new LoginResponse(accessToken);
    }
}
