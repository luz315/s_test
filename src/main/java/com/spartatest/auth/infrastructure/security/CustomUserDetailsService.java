package com.spartatest.auth.infrastructure.security;

import com.spartatest.auth.domain.entity.User;
import com.spartatest.auth.domain.repository.UserRepository;
import com.spartatest.common.exception.CustomException;
import com.spartatest.common.exception.custom.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(UserErrorCode.USERNAME_NOT_FOUND));
        return new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getRole()
        );
    }
}