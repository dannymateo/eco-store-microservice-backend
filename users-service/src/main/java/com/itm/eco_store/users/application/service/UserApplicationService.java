package com.itm.eco_store.users.application.service;

import com.itm.eco_store.users.domain.model.User;
import com.itm.eco_store.users.domain.model.UserInfo;
import com.itm.eco_store.users.domain.ports.in.IUserManagementUseCase;
import com.itm.eco_store.users.domain.ports.out.IPasswordEncoder;
import com.itm.eco_store.users.domain.ports.out.IUserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserApplicationService implements IUserManagementUseCase {

    private final IUserRepository userRepository;
    private final IPasswordEncoder passwordEncoder;

    public UserApplicationService(IUserRepository userRepository, IPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserInfo createUser(CreateUserCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        String passwordHash = passwordEncoder.encode(command.password());
        User user = User.create(command.email(), passwordHash, command.role());
        User savedUser = userRepository.save(user);

        return savedUser.toUserInfo();
    }

    @Override
    public UserInfo getUser(GetUserQuery query) {
        User user = userRepository.findById(query.id())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        return user.toUserInfo();
    }

    @Override
    public UserInfo updateUser(UpdateUserCommand command) {
        User user = userRepository.findById(command.id())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (command.email() != null && !command.email().equals(user.getEmail())) {
            if (userRepository.existsByEmail(command.email())) {
                throw new IllegalArgumentException("El email ya está en uso");
            }
            user.setEmail(command.email());
        }

        if (command.role() != null) {
            user.setRole(command.role());
        }

        if (command.active() != null) {
            user.setActive(command.active());
        }

        User updatedUser = userRepository.save(user);
        return updatedUser.toUserInfo();
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void resetPassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        String passwordHash = passwordEncoder.encode(newPassword);
        user.setPasswordHash(passwordHash);
        userRepository.save(user);
    }
}