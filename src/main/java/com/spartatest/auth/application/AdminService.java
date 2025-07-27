package com.spartatest.auth.application;

import com.spartatest.auth.domain.entity.Role;
import com.spartatest.auth.domain.entity.User;
import com.spartatest.auth.domain.repository.UserRepository;
import com.spartatest.auth.dto.response.GrantAdminResponse;
import com.spartatest.common.exception.CustomException;
import com.spartatest.common.exception.custom.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;

    public GrantAdminResponse grantAdminRole(String targetUsername, User currentUser) {
        if (!currentUser.getRole().equals(Role.ADMIN)) {
            throw new CustomException(UserErrorCode.ACCESS_DENIED);
        }

        User user = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        user.grantAdminRole();
        userRepository.save(user);
        return GrantAdminResponse.from(user);
    }
}
