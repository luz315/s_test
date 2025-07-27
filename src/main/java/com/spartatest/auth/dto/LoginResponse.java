package com.spartatest.auth.dto;

public record LoginResponse(String token) {
    public static LoginResponse from(String accessToken) {
        return new LoginResponse(accessToken);
    }
}
