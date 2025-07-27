package com.spartatest.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spartatest.auth.domain.entity.Role;
import com.spartatest.auth.domain.entity.User;
import com.spartatest.auth.domain.repository.UserRepository;
import com.spartatest.auth.dto.request.SignupRequest;
import com.spartatest.auth.infrastructure.jwt.JwtGenerator;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthIntegrationTest {
    @Autowired private JwtGenerator jwtGenerator;
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private BCryptPasswordEncoder passwordEncoder;

    private final String TEST_USERNAME = "testuser";
    private final String TEST_PASSWORD = "pass12";
    private final String TEST_NICKNAME = "Tester";

    @BeforeEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("정상적인 회원가입 요청 시 → 200 OK")
    void signup_success() throws Exception {
        // given
        SignupRequest request = new SignupRequest(TEST_USERNAME, TEST_PASSWORD, TEST_NICKNAME);

        // when & then
        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("중복된 사용자명으로 회원가입 요청 시 → 400 Bad Request")
    void signup_fail_duplicateUsername() throws Exception {
        // given
        User user = User.create(TEST_USERNAME, passwordEncoder.encode(TEST_PASSWORD), TEST_NICKNAME);
        userRepository.save(user);
        SignupRequest request = new SignupRequest(TEST_USERNAME, TEST_PASSWORD, "OtherNick");

        // when & then
        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(1001))
                .andExpect(jsonPath("$.message").value("이미 가입된 사용자입니다."))
                .andExpect(jsonPath("$.data").isEmpty());

    }

    @Test
    @DisplayName("올바른 자격 증명으로 로그인 요청 시 → 200 OK (AccessToken + Refresh 쿠키 발급)")
    void login_success() throws Exception {
        // given
        User user = User.create(TEST_USERNAME, passwordEncoder.encode(TEST_PASSWORD), TEST_NICKNAME);
        userRepository.save(user);

        String body = objectMapper.writeValueAsString(
                new SignupRequest(TEST_USERNAME, TEST_PASSWORD, TEST_NICKNAME));

        // when
        MvcResult result = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"))
                .andExpect(cookie().exists("refresh"))
                .andReturn();

        // then
        String responseContent = result.getResponse().getContentAsString();
        assertThat(responseContent).contains("accessToken");
    }

    @Test
    @DisplayName("잘못된 자격 증명으로 로그인 요청 시 → 401 Unauthorized")
    void login_fail_invalidCredentials() throws Exception {
        // given
        String body = objectMapper.writeValueAsString(
                new SignupRequest("wrong", "wrong", "wrong"));

        // when & then
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("관리자가 권한 부여 요청 시 → 200 OK")
    void grantAdminRole_success() throws Exception {
        // given
        User admin = User.create("admin", passwordEncoder.encode("admin1234"), "관리자");
        admin.grantAdminRole();
        userRepository.save(admin);

        User target = User.create("target", passwordEncoder.encode("1234"), "유저");
        userRepository.save(target);

        String token = generateTestToken(admin.getId(), admin.getUsername(), admin.getRole());


        // when & then
        mockMvc.perform(patch("/admin/target/roles")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.role").value("ADMIN"));
    }

    @Test
    @DisplayName("존재하지 않는 사용자에게 권한 부여 요청 시 → 404 Not Found")
    void grantAdminRole_fail_targetUserNotFound() throws Exception {
        // given
        User admin = User.create("admin", passwordEncoder.encode("admin1234"), "관리자");
        admin.grantAdminRole();
        userRepository.save(admin);

        String token = generateTestToken(admin.getId(), admin.getUsername(), admin.getRole());

        // when & then
        mockMvc.perform(patch("/admin/nonexistent/roles")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(1002))
                .andExpect(jsonPath("$.message").value("사용자를 찾을 수 없습니다."));
    }



    @Test
    @DisplayName("일반 사용자가 권한 부여 요청 시 → 403 Forbidden")
    void grantAdminRole_fail_nonAdmin() throws Exception {
        // given
        User user = User.create(TEST_USERNAME, passwordEncoder.encode(TEST_PASSWORD), TEST_NICKNAME);
        userRepository.save(user);

        String token = generateTestToken(user.getId(), user.getUsername(), user.getRole());

        // when & then
        mockMvc.perform(patch("/admin/someone/roles")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    // JWT 헬퍼
    private String generateTestToken(UUID userId, String username, Role role) {
        return jwtGenerator.createJwt(userId, "access", username, role, 1000 * 60L);
    }
}
