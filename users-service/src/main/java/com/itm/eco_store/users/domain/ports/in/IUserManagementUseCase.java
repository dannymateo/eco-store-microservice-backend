package com.itm.eco_store.users.domain.ports.in;

import com.itm.eco_store.users.domain.model.User;
import com.itm.eco_store.users.domain.model.UserInfo;

import java.util.Optional;

public interface IUserManagementUseCase {

    record CreateUserCommand(String email, String password, String role) {}

    record GetUserQuery(Long id) {}

    record UpdateUserCommand(Long id, String email, String role, Boolean active) {}

    UserInfo createUser(CreateUserCommand command);

    UserInfo getUser(GetUserQuery query);

    UserInfo updateUser(UpdateUserCommand command);

    Optional<User> getUserByEmail(String email);

    void resetPassword(Long userId, String newPassword);
}