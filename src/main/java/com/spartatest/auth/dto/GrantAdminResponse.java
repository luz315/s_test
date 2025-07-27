package com.spartatest.auth.dto;

import com.spartatest.auth.domain.entity.Role;
import com.spartatest.auth.domain.entity.User;

public record GrantAdminResponse(
        String username,
        String nickname,
        Role role
) {
    public static GrantAdminResponse from(User user) {
        return new GrantAdminResponse(
                user.getUsername(),
                user.getNickname(),
                user.getRole()
        );
    }
}
