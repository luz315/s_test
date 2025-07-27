package com.spartatest.auth.infrastructure.security;

import com.spartatest.auth.infrastructure.cookie.CookieUtil;
import com.spartatest.auth.infrastructure.jwt.JwtValidator;
import com.spartatest.common.exception.custom.AuthErrorCode;
import com.spartatest.common.exception.CustomException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
public class CustomLogoutFilter extends OncePerRequestFilter {

    private final JwtValidator jwtValidator;
    private final CookieUtil cookieUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getRequestURI().equals("/logout") && request.getMethod().equals("POST")) {

            Cookie[] cookies = request.getCookies();
            if (cookies == null) {
                throw new CustomException(AuthErrorCode.MISSING_COOKIE);
            }

            Optional<Cookie> refreshCookieOpt = Arrays.stream(cookies)
                    .filter(cookie -> "refresh".equals(cookie.getName()))
                    .findFirst();

            if (refreshCookieOpt.isEmpty()) {
                throw new CustomException(AuthErrorCode.MISSING_REFRESH_TOKEN);
            }

            String refreshToken = refreshCookieOpt.get().getValue();

            try {
                if (jwtValidator.isExpired(refreshToken)) {
                    throw new CustomException(AuthErrorCode.EXPIRED_TOKEN);
                }
            } catch (ExpiredJwtException e) {
                throw new CustomException(AuthErrorCode.EXPIRED_TOKEN);
            } catch (Exception e) {
                throw new CustomException(AuthErrorCode.INVALID_TOKEN);
            }

            String category = jwtValidator.getCategory(refreshToken);
            if (!"refresh".equals(category)) {
                throw new CustomException(AuthErrorCode.INVALID_TOKEN_TYPE);
            }

            cookieUtil.deleteRefreshToken(response);
            SecurityContextHolder.clearContext();
            return;
        }

        filterChain.doFilter(request, response);
    }
}
