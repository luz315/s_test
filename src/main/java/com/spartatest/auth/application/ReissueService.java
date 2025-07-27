package com.spartatest.auth.application;

import com.spartatest.auth.domain.entity.User;
import com.spartatest.auth.domain.repository.UserRepository;
import com.spartatest.auth.infrastructure.cookie.CookieUtil;
import com.spartatest.auth.infrastructure.jwt.JwtGenerator;
import com.spartatest.auth.infrastructure.jwt.JwtValidator;
import com.spartatest.common.exception.custom.AuthErrorCode;
import com.spartatest.common.exception.CustomException;
import com.spartatest.common.exception.custom.UserErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReissueService {

    private final UserRepository userRepository;
    private final JwtGenerator jwtGenerator;
    private final JwtValidator jwtValidator;
    private final CookieUtil cookieUtil;

    public void reissue(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookieUtil.getRefreshToken(request);
        if (refreshToken == null || jwtValidator.isExpired(refreshToken)) {
            throw new CustomException(AuthErrorCode.EXPIRED_TOKEN);
        }

        String category = jwtValidator.getCategory(refreshToken);
        if (!"refresh".equals(category)) {
            throw new CustomException(AuthErrorCode.INVALID_TOKEN_TYPE);
        }

        UUID userId = jwtValidator.getUserId(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        String newAccessToken = jwtGenerator.createJwt(user.getId(), "access", user.getUsername(), user.getRole(), 1000 * 60 * 30L);
        String newRefreshToken = jwtGenerator.createJwt(user.getId(), "refresh", user.getUsername(), user.getRole(), 1000 * 60 * 60 * 24L);

        response.setHeader("Authorization", "Bearer " + newAccessToken);
        response.addCookie(cookieUtil.createCookie(newRefreshToken));
    }
}