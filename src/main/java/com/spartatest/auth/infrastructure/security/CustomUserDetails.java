package com.spartatest.auth.infrastructure.security;

import com.spartatest.auth.domain.entity.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
public class CustomUserDetails implements UserDetails {
    private final UUID userId;
    private final String username;
    private final String password;
    private final Role role;

    public CustomUserDetails(UUID userId, String username, String password, Role role) {
        this.userId = userId;
        this.username = username;
        this.password = password; // 실제 비밀번호 (암호화된) 저장
        this.role = role;
    }

    public CustomUserDetails(UUID userId, String username, Role role) {
        this.userId = userId;
        this.username = username;
        this.password = null; // JWT 인증 시에는 비밀번호가 필요 없으므로 null로 설정
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_"+role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getPassword() {
        if (this.password == null) {
            return null;
        }
        return this.password;
    }
} 