package com.spartatest.common.init;

import com.spartatest.auth.domain.entity.User;
import com.spartatest.auth.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminAccountInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${admin.username}") private String username;
    @Value("${admin.password}") private String password;
    @Value("${admin.nickname}") private String nickname;

    @Override
    public void run(ApplicationArguments args) {
        userRepository.findByUsername(username).ifPresentOrElse(
            u -> {}, // 이미 있으면 생략
            () -> {
                User user = User.createAdminForTest(
                        username,
                        passwordEncoder.encode(password)
                        ,nickname
                );
                userRepository.save(user);
            }
        );
    }
}
