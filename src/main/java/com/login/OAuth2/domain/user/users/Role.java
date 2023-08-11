package com.login.OAuth2.domain.user.users;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    GUEST("ROLE_GUEST"), USER("ROLE_USER");

    private final String key;
}
