package com.spartatest.auth.domain.repository;

import com.spartatest.auth.domain.entity.User;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<String, User> usersByUsername = new HashMap<>();
    private final Map<UUID, User> usersById = new HashMap<>();

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(usersByUsername.get(username));
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(usersById.get(id));
    }

    @Override
    public void save(User user) {
        usersByUsername.put(user.getUsername(), user);
        usersById.put(user.getId(), user);
    }

    @Override
    public void deleteById(UUID id) {
        findById(id).ifPresent(user -> usersByUsername.remove(user.getUsername()));
        usersById.remove(id);
    }

    @Override
    public void clear() {
        usersByUsername.clear();
        usersById.clear();
    }

    @Override
    public void deleteAll() {
        usersByUsername.clear();
        usersById.clear();
    }
}
