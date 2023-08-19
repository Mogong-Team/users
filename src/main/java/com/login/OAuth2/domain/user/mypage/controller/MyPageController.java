package com.login.OAuth2.domain.user.mypage.controller;

import com.login.OAuth2.domain.user.users.User;
import com.login.OAuth2.domain.user.users.service.UserService;
import com.login.OAuth2.domain.user.util.ProfileImageUtil;
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

        Long userId = jwtService.getUserId(request);

        if(userId != null) {
            User user = userService.findUser(userId);

            if (user != null) {
                if (userService.isNicknameExists(newNickname)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("New nickname already exists.");
                }
                userService.changeNickname(user, newNickname);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/change-profile")
    public ResponseEntity<String> changeProfile(HttpServletRequest request, @RequestParam("imageUrl") String imageUrl){

        Long userId = jwtService.getUserId(request);

        if(userId != null){
            User user = userService.findUser(userId);

            if(user == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            userService.changeImageUrl(user, imageUrl);

            return ResponseEntity.status(HttpStatus.OK).body("Update user's profile");
        } else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @PostMapping("/change-profile/basic")
    public ResponseEntity<String> changeProfileBasic(HttpServletRequest request){

        Long userId = jwtService.getUserId(request);

        if(userId != null){
            User user = userService.findUser(userId);

            if(user == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            String randomImageUrl = ProfileImageUtil.getRandomImageUrl();
            userService.changeImageUrl(user, randomImageUrl);
            
            return ResponseEntity.status(HttpStatus.OK).build();
        } else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @PostMapping("/duplicate-check/nickname")
    public ResponseEntity<String> duplicateCheck(@RequestParam("nickname") String nickname){

        if(userService.isNicknameExists(nickname)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nickname already exists.");
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
