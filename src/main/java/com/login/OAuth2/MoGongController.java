package com.login.OAuth2;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MoGongController {

    @GetMapping("/home")
    public String index(){
        return "Hello World!";
    }
}
