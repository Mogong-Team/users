package com.login.OAuth2.global.oauth2.controller;

import com.login.OAuth2.domain.user.Role;
import com.login.OAuth2.domain.user.User;
import com.login.OAuth2.domain.user.repository.UserRepository;
import com.login.OAuth2.domain.user.service.UserService;
import com.login.OAuth2.global.jwt.service.JwtService;
import com.login.OAuth2.global.oauth2.CustomOAuth2User;
import com.nimbusds.jose.util.BoundedInputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.swing.text.html.parser.Entity;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/oauth2")
public class OAuth2Controller {

    private final JwtService jwtService;
    private final UserService userService;

    @GetMapping("/sign-up")
    public String oAuthSignUp(){
        log.info(">> OAuth2Controller.oAuthSignUp 실행 : /oauth2/sign-up : 여기서 닉네임을 받는다 접근");
        return "signup";
    }

    @PostMapping("/sign-up")
    public ResponseEntity<String> setNickname(HttpServletRequest request, @RequestParam("nickname") String nickname){
        log.info(">> OAuth2Controller.setNicname() 실행 - 닉네임 : {}", nickname);

        Optional<String> accessToken = jwtService.extractAccessToken(request);
        Optional<Long> userId = jwtService.extractUserId(accessToken.get());


        if(userId.isPresent()){
            if(userService.isNicknameExists(nickname)){
                log.error(">> >> >> Error: Nickname already exists.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nickname already exists. Please choose a different nickname.");
            }
            userService.updateNickname(userService.findUser(userId.get()), nickname);
            log.info(">> >> after updateNickname : " + nickname);
        } else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found.");
        }

        /**
         * 그럼 여기서 뭐 액토으로 유저를 찾아서 업데이트 해주는 ㄱ ㅣ아니라
         * 여기서 생성. 유저. 그 전에 게스트도 줄 필요가 없다.
         *
         * */
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/duplicate-check")
    public ResponseEntity<String> duplicateCheck(@RequestParam("nickname") String nickname){

        if(userService.isNicknameExists(nickname)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nickname already exists.");
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
