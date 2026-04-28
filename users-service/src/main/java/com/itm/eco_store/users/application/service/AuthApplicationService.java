package com.itm.eco_store.users.application.service;

import com.itm.eco_store.users.domain.model.User;
import com.itm.eco_store.users.domain.model.UserInfo;
import com.itm.eco_store.users.domain.ports.in.IAuthUseCase;
import com.itm.eco_store.users.domain.ports.in.IUserManagementUseCase;
import com.itm.eco_store.users.domain.ports.out.IPasswordEncoder;
import com.itm.eco_store.users.domain.ports.out.IUserRepository;
import com.itm.eco_store.users.domain.service.TokenService;
import org.springframework.stereotype.Service;

@Service
public class AuthApplicationService implements IAuthUseCase {

    private final IUserRepository userRepository;
    private final IPasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthApplicationService(IUserRepository userRepository, IPasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @Override
    public AuthResponse login(LoginCommand command) {
        User user = userRepository.findByEmail(command.email())
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));

        if (!passwordEncoder.matches(command.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        if (!user.isActive()) {
            throw new IllegalStateException("Usuario desactivado");
        }

        String token = tokenService.generateToken(user);
        return new AuthResponse(token, user.toUserInfo());
    }

    @Override
    public UserInfo register(RegisterCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        String passwordHash = passwordEncoder.encode(command.password());
        User user = User.create(command.email(), passwordHash, command.role());
        User savedUser = userRepository.save(user);

        return savedUser.toUserInfo();
    }
}