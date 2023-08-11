package com.login.OAuth2.domain.user.mypage.controller;

import com.login.OAuth2.domain.user.users.User;
import com.login.OAuth2.domain.user.users.service.UserService;
import com.login.OAuth2.global.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class MyPageController {

    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping("/change-nickname")
    public ResponseEntity<String> changeNickname(HttpServletRequest request, @RequestParam("nickname") String newNickname){
        log.info(">> MyPageController.changeNickname() 호출");

        Optional<String> accessToken = jwtService.extractAccessToken(request);
        Optional<Long> userId = jwtService.extractUserId(accessToken.get());

        if(userId.isPresent()) {
            User user = userService.findUser(userId.get());

            if (user != null) {
                if (userService.isNicknameExists(newNickname)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("New nickname already exists.");
                }
                userService.updateNickname(user, newNickname);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

//    @PostMapping("/change-profile")
//    public ResponseEntity<String> changeProfile(HttpServletRequest request, String imageUrl){
//
//    }
}
