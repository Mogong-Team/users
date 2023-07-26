package com.login.OAuth2.global.oauth2.handler;

import com.login.OAuth2.domain.user.Role;
import com.login.OAuth2.domain.user.User;
import com.login.OAuth2.domain.user.repository.UserRepository;
import com.login.OAuth2.domain.user.service.UserService;
import com.login.OAuth2.global.jwt.service.JwtService;
import com.login.OAuth2.global.oauth2.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
//    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login 성공!");
        System.out.println(">> OAuth2LoginSuccessHandler.onAuthenticationSuccess() 실행 - OAuth2 Login 성공!");
        try {
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
            System.out.println(">> >> User Role : " + oAuth2User.getRole());

            // User의 Role이 GUEST일 경우 처음 요청한 회원이므로 회원가입 페이지로 리다이렉트
            if(oAuth2User.getRole() == Role.GUEST) {
                String accessToken = jwtService.createAccessToken(oAuth2User.getEmail());
                String refreshToken = jwtService.createRefreshToken();
                response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
                response.addHeader(jwtService.getRefreshHeader(), "Bearer " + refreshToken);

                User findUser = userRepository.findByEmail(oAuth2User.getEmail())
                        .orElseThrow(() -> new IllegalArgumentException("이메일에 해당하는 유저가 없습니다."));

                jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
                jwtService.updateRefreshToken(oAuth2User.getEmail(), refreshToken);
                response.sendRedirect("/oauth2/sign-up/" + findUser.getId()); // 프론트의 회원가입 추가 정보 입력 폼으로 리다이렉트
//                System.out.println(">> >> findUser : " + findUser.getRole());
//                findUser.authorizeUser();
//                System.out.println(">> >> findUser : " + findUser.getRole());
//                userRepository.save(findUser);
            } else {
                loginSuccess(response, oAuth2User); // 로그인에 성공한 경우 access, refresh 토큰 생성
            }
        } catch (Exception e) {
            throw e;
        }
    }

    // TODO : 소셜 로그인 시에도 무조건 토큰 생성하지 말고 JWT 인증 필터처럼 RefreshToken 유/무에 따라 다르게 처리해보기
    private void loginSuccess(HttpServletResponse response, CustomOAuth2User oAuth2User) throws IOException {
        System.out.println(">> SuccessHandler.loginSuccess() 호출");
        String accessToken = jwtService.createAccessToken(oAuth2User.getName());
        String refreshToken = jwtService.createRefreshToken();
        response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
        response.addHeader(jwtService.getRefreshHeader(), "Bearer " + refreshToken);

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
        jwtService.updateRefreshToken(oAuth2User.getEmail(), refreshToken);
    }
}
