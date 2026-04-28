package com.itm.eco_store.users.domain.ports.in;

import com.itm.eco_store.users.domain.model.UserInfo;

public interface IAuthUseCase {

    record LoginCommand(String email, String password) {}

    record RegisterCommand(String email, String password, String role) {}

    record AuthResponse(String token, UserInfo userInfo) {}

    AuthResponse login(LoginCommand command);

    UserInfo register(RegisterCommand command);
}