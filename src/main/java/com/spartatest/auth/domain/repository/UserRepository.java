package com.spartatest.auth.domain.repository;

import com.spartatest.auth.domain.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findByUsername(String username);
    Optional<User> findById(UUID id);
    void save(User user);
    void deleteById(UUID id);
    void clear();
    void deleteAll();
}
