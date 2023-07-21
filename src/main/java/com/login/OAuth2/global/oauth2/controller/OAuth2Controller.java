package com.login.OAuth2.global.oauth2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/oauth2")
public class OAuth2Controller {

    @GetMapping("/sign-up")
    public String oAuthSignUp(){
        System.out.println(">> OAuth2Controller.oAuthSignUp 실행 : /oauth2/sign-up : 여기서 닉네임을 받는다 접근");
        return "signup";
    }

    @RequestMapping(value = "/sign-up", method = RequestMethod.POST)
    public String setNickname(@RequestParam("nickname") String nickname){
        System.out.println(">> OAuth2Controller.setNicname() 실행 - 닉네임 : " + nickname);

        return "redirect:/home";
    }
}
