package com.login.OAuth2.domain.user.badge.controller;

import com.login.OAuth2.domain.user.badge.dto.BadgeResponseDto;
import com.login.OAuth2.domain.user.badge.repository.BadgeRepository;
import com.login.OAuth2.domain.user.badge.service.BadgeService;
import com.login.OAuth2.domain.user.users.User;
import com.login.OAuth2.domain.user.users.service.UserService;
import com.login.OAuth2.global.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
public class BadgeController {

    private final JwtService jwtService;
    private final UserService userService;
    private final BadgeService badgeService;

    @GetMapping("/users/badges")
    public ResponseEntity<List<BadgeResponseDto>> getUserBadges(HttpServletRequest request){

        Long userId = jwtService.getUserId(request);

        if(userId != null){
            User user = userService.findUser(userId);

            List<BadgeResponseDto> badgeResponseDtoList = user.getBadges().stream()
                    .map(badge -> new BadgeResponseDto(badge.getImageUrl(), badge.getName()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(badgeResponseDtoList);
        } else{
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/users/take-badge")
    public ResponseEntity<String> takeBadge(HttpServletRequest request, @RequestParam("badgeName") String badgeName){

        Long userId = jwtService.getUserId(request);

        if(badgeService.takeBadge(userId, badgeName)){
            return ResponseEntity.status(HttpStatus.OK).build();
        } else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
