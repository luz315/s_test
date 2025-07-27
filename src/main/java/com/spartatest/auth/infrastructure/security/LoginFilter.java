package com.spartatest.auth.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spartatest.auth.domain.entity.User;
import com.spartatest.auth.domain.repository.UserRepository;
import com.spartatest.auth.infrastructure.jwt.JwtGenerator;
import com.spartatest.auth.infrastructure.cookie.CookieUtil;
import com.spartatest.common.exception.custom.AuthErrorCode;
import com.spartatest.common.exception.CustomException;
import com.spartatest.common.exception.custom.UserErrorCode;
import com.spartatest.common.response.ApiResult;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtGenerator jwtGenerator;
    private final CookieUtil cookieUtil;
    private final UserRepository userRepository;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            Map<String, String> credentials = new ObjectMapper().readValue(request.getInputStream(), Map.class);
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(credentials.get("username"), credentials.get("password")));
        } catch (IOException e) {
            throw new CustomException(AuthErrorCode.MALFORMED_REQUEST_BODY);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));


        String accessToken = jwtGenerator.createJwt(user.getId(), "access", user.getUsername(), user.getRole(), 1000 * 60 * 30L);
        String refreshToken = jwtGenerator.createJwt(user.getId(), "refresh", user.getUsername(), user.getRole(), 1000 * 60 * 60 * 24L);

        response.setHeader("Authorization", "Bearer " + accessToken);
        response.addCookie(cookieUtil.createCookie(refreshToken));
        response.setStatus(HttpStatus.OK.value());

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        new ObjectMapper().writeValue(response.getWriter(), Map.of(
                "accessToken", "Bearer " + accessToken
        ));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ApiResult<Void> errorResponse = ApiResult.error(HttpStatus.UNAUTHORIZED, AuthErrorCode.INVALID_CREDENTIALS.getMessage());
        new ObjectMapper().writeValue(response.getWriter(), errorResponse);
    }

}
