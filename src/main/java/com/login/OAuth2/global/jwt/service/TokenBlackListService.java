package com.login.OAuth2.global.jwt.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class TokenBlackListService {

    private final Set<String> blackList = new HashSet<>();

    public void blackListToken(String token){
        log.info(">> TokenBlackListService.blackListToken() 호출 - 토큰 블랙리스트에 넣음");
        blackList.add(token);
    }

    public boolean isTokenBlacklisted(String token) {
        return blackList.contains(token);
    }
}
