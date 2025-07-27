package com.spartatest.auth;

import com.spartatest.auth.domain.entity.Role;
import com.spartatest.auth.domain.entity.User;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.spartatest.auth.domain.repository.UserRepository;
import com.spartatest.auth.infrastructure.jwt.JwtGenerator;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
class AuthJwtTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private JwtGenerator jwtGenerator;
    @Autowired private BCryptPasswordEncoder passwordEncoder;

    private String accessTokenUser;
    private String accessTokenAdmin;

    @BeforeEach
    void setup() {
        // given
        User user = User.create("user", passwordEncoder.encode("1234"), "유저");
        User admin = User.create("admin", passwordEncoder.encode("1234"), "관리자");
        admin.grantAdminRole();

        userRepository.save(user);
        userRepository.save(admin);

        accessTokenUser = "Bearer " + jwtGenerator.createJwt(user.getId(), "access", user.getUsername(), user.getRole(), 1000 * 60 * 30L);
        accessTokenAdmin = "Bearer " + jwtGenerator.createJwt(admin.getId(), "access", admin.getUsername(), admin.getRole(), 1000 * 60 * 30L);
    }

    @Test
    @DisplayName("관리자가 권한 부여 요청 시 → 200 OK")
    void grantAdminRoleWithAdmin() throws Exception {
        // when & then
        mockMvc.perform(patch("/admin/{targetUsername}/roles", "user")
                        .header("Authorization", accessTokenAdmin))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("일반 유저가 권한 부여 요청 시 → 403 Forbidden")
    void grantAdminRoleWithUserRole() throws Exception {
        // when & then
        mockMvc.perform(patch("/admin/{targetUsername}/roles", "admin")
                        .header("Authorization", accessTokenUser))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("토큰 없이 권한 부여 요청 시 → 401 Unauthorized")
    void grantAdminRoleWithoutToken() throws Exception {
        // when & then
        mockMvc.perform(patch("/admin/{targetUsername}/roles", "admin"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("서명이 변조된 토큰 사용 시 → CustomException 발생")
    void grantAdminRoleWithTamperedToken() {
        // given
        String validJwt = jwtGenerator.createJwt(
                UUID.randomUUID(), "access", "fakeUser", Role.USER, 1000L
        );
        String[] parts = validJwt.split("\\.");
        String tamperedJwt = "Bearer " + parts[0] + "." + parts[1] + ".WRONG_SIGNATURE";

        // when & then
        assertThrows(SignatureException.class, () ->
                mockMvc.perform(patch("/admin/{targetUsername}/roles", "admin")
                        .header("Authorization", tamperedJwt))
        );
    }

    @Test
    @DisplayName("만료된 토큰 사용 시 → 401 Unauthorized")
    void grantAdminRoleWithExpiredToken() {
        // given
        String expiredToken = "Bearer " + jwtGenerator.createJwt(
                UUID.randomUUID(), "access", "expiredUser", Role.USER, -1000L
        );

        // when & then
        assertThrows(Exception.class, () ->
                mockMvc.perform(patch("/admin/{targetUsername}/roles", "admin")
                        .header("Authorization", expiredToken))
        );
    }
}
