package com.itm.eco_store.users.domain.ports.out;

import com.itm.eco_store.users.domain.model.User;

import java.util.Optional;

public interface IUserRepository {
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    User save(User user);
}