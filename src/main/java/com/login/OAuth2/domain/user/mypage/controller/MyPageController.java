package com.login.OAuth2.domain.user.mypage.controller;

import com.login.OAuth2.domain.user.User;
import com.login.OAuth2.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class MyPageController {

    private final UserService userService;

    @PostMapping("/{id}/change-nickname")
    public ResponseEntity<String> changeNickname(@PathVariable Long id, @RequestParam("nickname") String newNickname){
        System.out.println(">> MyPageController.changeNickname() 호출");

        User user = userService.findUser(id);

        if(user != null){
            if(userService.isNicknameExists(newNickname)){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("New nickname already exists.");
            }
            userService.updateNickname(user, newNickname);
        } else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
