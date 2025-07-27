package com.spartatest.auth.dto.response;

import com.spartatest.auth.domain.entity.Role;
import com.spartatest.auth.domain.entity.User;

public record SignupResponse(
        String username,
        String nickname,
        Role roles
) {
    public static SignupResponse from(User user) {
        return new SignupResponse(
                user.getUsername(),
                user.getNickname(),
                user.getRole());
    }
}
