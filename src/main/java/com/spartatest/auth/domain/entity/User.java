package com.spartatest.auth.domain.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User {
    private UUID id;
    private String username;
    private String password;
    private String nickname;
    private Role role;


    @Builder(access = AccessLevel.PRIVATE)
    private User(String username, String password, String nickname, Role role) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
    }

    public static User create(String username, String password, String nickname) {
        User user = User.builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .role(Role.USER)
                .build();
        user.id = UUID.randomUUID();
        return user;
    }

    public void grantAdminRole() {
        this.role = Role.ADMIN;
    }

    public static User createAdminForTest(String username, String password, String nickname) {
        User user = User.builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .role(Role.ADMIN)
                .build();
        user.id = UUID.randomUUID();
        return user;
    }

}
