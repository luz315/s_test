package com.spartatest.auth.infrastructure.security;

import com.spartatest.auth.domain.entity.Role;
import com.spartatest.auth.infrastructure.cookie.CookieUtil;
import com.spartatest.auth.infrastructure.jwt.JwtValidator;
import com.spartatest.common.exception.custom.AuthErrorCode;
import com.spartatest.common.exception.CustomException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtValidator jwtValidator;
    private final CookieUtil cookieUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        if (!"access".equals(jwtValidator.getCategory(token))) {
            throw new CustomException(AuthErrorCode.INVALID_TOKEN_TYPE);
        }

        if (jwtValidator.isExpired(token)) {
            String refreshToken = cookieUtil.getRefreshToken(request);
            if (refreshToken == null || jwtValidator.isExpired(refreshToken)) {
                throw new CustomException(AuthErrorCode.EXPIRED_TOKEN);
            }

            filterChain.doFilter(request, response);
            return;
        }

        try {
            UUID userId = jwtValidator.getUserId(token);
            String username = jwtValidator.getUsername(token);
            Role role = jwtValidator.getRole(token);

            CustomUserDetails userDetails = new CustomUserDetails(userId, username, role);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (MalformedJwtException e) {
            throw new CustomException(AuthErrorCode.INVALID_TOKEN);
        }

        filterChain.doFilter(request, response);
    }
}
