package com.spartatest.auth.dto;

public record SignupRequest(
        String username,
        String password,
        String nickname
) {}
