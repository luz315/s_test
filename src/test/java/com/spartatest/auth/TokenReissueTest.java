package com.spartatest.auth;

import com.spartatest.auth.domain.repository.UserRepository;
import com.spartatest.auth.dto.request.SignupRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import jakarta.servlet.http.Cookie;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TokenReissueTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private BCryptPasswordEncoder passwordEncoder;
    @Autowired private ObjectMapper objectMapper;

    private final String USERNAME = "test";
    private final String PASSWORD = "1234";
    private final String NICKNAME = "테스트";

    @BeforeEach
    void clearDatabase() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("유효한 리프레시 토큰으로 재발급 요청 시 → 200 OK & Authorization 헤더 존재")
    void reissueTokenWithValidRefresh() throws Exception {
        // given
        SignupRequest signupRequest = new SignupRequest("test", "1234", "테스트");
        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk());

        MvcResult loginResult = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String setCookieHeader = loginResult.getResponse().getHeader("Set-Cookie");
        assertThat(setCookieHeader).isNotNull();
        String refreshValue = setCookieHeader.split(";")[0].split("=")[1];
        Cookie refreshCookie = new Cookie("refresh", refreshValue);

        // when & then
        MvcResult reissueResult = mockMvc.perform(post("/reissue")
                        .cookie(refreshCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(header().exists("Authorization"))
                .andReturn();

        String newAccessToken = reissueResult.getResponse().getHeader("Authorization");
        assertThat(newAccessToken).isNotNull();
        assertThat(newAccessToken).startsWith("Bearer ");
    }

}
