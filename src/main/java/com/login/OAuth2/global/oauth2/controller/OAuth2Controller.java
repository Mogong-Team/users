package com.login.OAuth2.global.oauth2.controller;

import com.login.OAuth2.domain.user.Role;
import com.login.OAuth2.domain.user.User;
import com.login.OAuth2.domain.user.repository.UserRepository;
import com.login.OAuth2.domain.user.service.UserService;
import com.login.OAuth2.global.jwt.service.JwtService;
import com.login.OAuth2.global.oauth2.CustomOAuth2User;
import com.nimbusds.jose.util.BoundedInputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;


@RequiredArgsConstructor
@Controller
@RequestMapping("/oauth2")
public class OAuth2Controller {

    private final JwtService jwtService;
    private final UserService userService;

    @GetMapping("/sign-up/{id}")
    public String oAuthSignUp(){
        System.out.println(">> OAuth2Controller.oAuthSignUp 실행 : /oauth2/sign-up : 여기서 닉네임을 받는다 접근");
        return "signup";
    }

    @PostMapping("/sign-up/{id}")
    public String setNickname(@PathVariable Long id, @RequestParam("nickname") String nickname){
        System.out.println(">> OAuth2Controller.setNicname() 실행 - 유저아이디 : " + id + " 닉네임 : " + nickname);

        User user = userService.findUser(id);
        System.out.println(">> >> after findUser : " + user.getNickname());

        if(user != null){
            userService.updateNickname(user, nickname);
            System.out.println(">> >> after updateNickname : " + user.getNickname());

            if(user.getRole() == Role.GUEST){
                userService.updateUserRole(user);
            }

        }

        /**
         * 그럼 여기서 뭐 액토으로 유저를 찾아서 업데이트 해주는 ㄱ ㅣ아니라
         * 여기서 생성. 유저. 그 전에 게스트도 줄 필요가 없다.
         *
         * */


        return "redirect:/home";
    }
}
