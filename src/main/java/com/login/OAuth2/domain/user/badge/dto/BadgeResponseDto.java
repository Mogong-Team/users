package com.login.OAuth2.domain.user.badge.dto;

import lombok.*;

@Getter
public class BadgeResponseDto {

    private String imageUrl;
    private String name;

    public BadgeResponseDto(String imageUrl, String name) {
        this.imageUrl = imageUrl;
        this.name = name;
    }
}
