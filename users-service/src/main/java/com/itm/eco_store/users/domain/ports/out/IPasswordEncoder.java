package com.itm.eco_store.users.domain.ports.out;

public interface IPasswordEncoder {
    String encode(CharSequence rawPassword);
    boolean matches(CharSequence rawPassword, String encodedPassword);
}