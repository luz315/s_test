package com.spartatest.auth.dto;

public record LoginRequest(
        String username,
        String password
) {}
