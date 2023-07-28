package com.login.OAuth2.global.logout.handler;

import com.login.OAuth2.global.jwt.service.JwtService;
import com.login.OAuth2.global.jwt.service.TokenBlackListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final JwtService jwtService;
    private final TokenBlackListService tokenBlackListService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        System.out.println(">> CustomLogoutSuccessHandler.onLogoutSuccess() 호출");

        try{
            Optional<String> accessToken = jwtService.extractAccessToken(request);
            if(accessToken.isPresent()){
                String token = accessToken.get();
                tokenBlackListService.blackListToken(token);
            }else{
                System.out.println(">> >> request에 액토가 없다");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        response.setStatus(HttpServletResponse.SC_OK);
        System.out.println(">> >> /login 페이지로 이동한다");
        response.sendRedirect("/login");
    }
}
