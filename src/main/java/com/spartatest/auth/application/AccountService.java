package com.spartatest.auth.application;

import com.spartatest.auth.domain.entity.User;
import com.spartatest.auth.domain.repository.UserRepository;
import com.spartatest.auth.dto.request.SignupRequest;
import com.spartatest.auth.dto.response.SignupResponse;
import com.spartatest.common.exception.CustomException;
import com.spartatest.common.exception.custom.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public SignupResponse signup(SignupRequest request) {
        userRepository.findByUsername(request.username()).ifPresent(u -> {
            throw new CustomException(UserErrorCode.USER_ALREADY_EXISTS);
        });

        User user = User.create(request.username(), bCryptPasswordEncoder.encode(request.password()), request.nickname());
        userRepository.save(user);
        return SignupResponse.from(user);
    }

    public void deleteAccount(UUID userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new CustomException(UserErrorCode.USER_NOT_FOUND);
        }
        userRepository.deleteById(userId);
    }
}
