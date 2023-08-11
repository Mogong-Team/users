package com.login.OAuth2.global.jwt.filter;

import com.login.OAuth2.domain.user.users.User;
import com.login.OAuth2.domain.user.users.repository.UserRepository;
import com.login.OAuth2.global.jwt.service.JwtService;
import com.login.OAuth2.global.jwt.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * Jwt 인증 필터
 * "/login" 이외의 URI 요청이 왔을 때 처리하는 필터
 *
 * 기본적으로 사용자는 요청 헤더에 AccessToken만 담아서 요청
 * AccessToken 만료 시에만 RefreshToken을 요청 헤더에 AccessToken과 함께 요청
 *
 * 1. RefreshToken이 없고, AccessToken이 유효한 경우 -> 인증 성공 처리, RefreshToken을 재발급하지는 않는다.
 * 2. RefreshToken이 없고, AccessToken이 없거나 유효하지 않은 경우 -> 인증 실패 처리, 403 ERROR
 * 3. RefreshToken이 있는 경우 -> DB의 RefreshToken과 비교하여 일치하면 AccessToken 재발급, RefreshToken 재발급(RTR 방식)
 *                              인증 성공 처리는 하지 않고 실패 처리
 *
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    private static final String NO_CHECK_URL = "/login"; // "/login"으로 들어오는 요청은 Filter 작동 X
    private static final String OAUTH_SIGNUP_URL = "/oauth2/sign-up";

    private final JwtService jwtService;
    private final UserRepository userRepository;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("\n>> JwtAuthenticationProcessingFilter.doFilterInternal() 실행 - 인증 처리/인증 실패/토큰 재발급 로직 수행");
        log.info(">> > request : " + request.getRequestURI());

        if (request.getRequestURI().equals(NO_CHECK_URL)) {
            log.info(">> >> URL check - /login 요청");
            filterChain.doFilter(request, response); // "/login" 요청이 들어오면, 다음 필터 호출
            return; // return으로 이후 현재 필터 진행 막기 (안해주면 아래로 내려가서 계속 필터 진행시킴)
        }

        Optional<String> token = jwtService.extractAccessToken(request);

        // 액토 검사
        if(token.isPresent()){
            if(jwtService.isTokenValid(token.get())) {
                Optional<Long> userId = jwtService.extractUserId(token.get());

                if (userId.isPresent()) {
                    userRepository.findById(userId.get()).ifPresent(this::saveAuthentication);
                    log.info("요청 유저의 인증정보 저장");
                }
            } else{
                log.info("Access Token이 유효하지 않음");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);    // Access Token Error : 401
                return;
            }
        }else{  // 리토 검사
            log.info("Access Token이 없음");

            String refreshToken = jwtService.extractRefreshToken(request)
                    .filter(jwtService::isTokenValid)
                    .orElse(null);

            if(refreshToken != null){
                checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
                response.setStatus(HttpServletResponse.SC_OK);
                return;
            }

            log.info("Access Token, Refresh Token 모두 없음");
        }

        filterChain.doFilter(request, response);


//        log.info(">> >> 요청 헤더에서 refreshToken 추출 - /login 요청이 아니기 때문에");
//        String refreshToken = jwtService.extractRefreshToken(request)   //토큰 헤더에서 refreshToken추출
//                .filter(jwtService::isTokenValid)   //유효성을 검사
//                .orElse(null);  //유효하지 않으면 null 반환
//
//        if (refreshToken != null) { //헤더에 리토가 있으면
//            log.info(">> >> >> refreshToken이 null이 아니다");
//            checkRefreshTokenAndReIssueAccessToken(response, refreshToken); //메서드 실행
//            return; // RefreshToken을 보낸 경우에는 AccessToken을 재발급 하고 인증 처리는 하지 않게 하기위해 바로 return으로 필터 진행 막기
//        }
//
//        if (refreshToken == null) { //헤더에 리토가 없으면
//            log.info(">> >> >> refreshToken이 null이다 - AccessToken을 검사해야 한다");
//            checkAccessTokenAndAuthentication(request, response, filterChain);  //액토가 유효한지를 판단함
//        }
    }

    /**
     *  [리프레시 토큰으로 유저 정보 찾기 & 액세스 토큰/리프레시 토큰 재발급 메소드]
     */
    public void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) { //헤더에 리토가 이씅면 실행됨
        log.info(">> JwtAuthenticationProcessingFilter.checkRefreshTokenAndReIssueAccessToken() 실행 - 헤더에 리토가 있기 때문에 실행된다.");
        userRepository.findByRefreshToken(refreshToken) //헤더에서 추출한 리토로 DB에서 유저를 일단 찾음
                .ifPresent(user -> {    //해당 유저가 있으면
                    String reIssuedRefreshToken = reIssueRefreshToken(user);    //유저에 대한 리토를 재발급해주고 리토 DB에 새로 저장
                    jwtService.sendAccessAndRefreshToken(response, jwtService.createAccessToken(user.getId()), reIssuedRefreshToken);
                    //유저에 대한 액토를 재발급하고 sendAccessAndRefreshToken 메서드를 호출해 재발급한 액토, 리토를 response에 보낸다. (헤더에 붙인다)
                });
    }

    /**
     * [리프레시 토큰 재발급 & DB에 리프레시 토큰 업데이트 메소드]
     */
    private String reIssueRefreshToken(User user) {
        String reIssuedRefreshToken = jwtService.createRefreshToken();  //리토 재발급
        user.updateRefreshToken(reIssuedRefreshToken);  //유저 리토 필드 update
        userRepository.saveAndFlush(user);  //DB에 저장
        return reIssuedRefreshToken;
    }

    /**
     * [액세스 토큰 체크 & 인증 처리 메소드]
     */
    public void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                  FilterChain filterChain) throws ServletException, IOException {   //액토 체크, 인증
        log.info(">> JwtAuthenticationProcessingFilter.checkAccessTokenAndAuthentication() 호출");

        Optional<String> accessToken = jwtService.extractAccessToken(request);
        log.info(">> >> accessToken : {}", accessToken);
        if (accessToken.isPresent() && jwtService.isTokenValid(accessToken.get())) {
            Optional<Long> UserId = jwtService.extractUserId(accessToken.get());
            log.info("User Id : {}", UserId);
            if (UserId.isPresent()) {
                userRepository.findById(Long.valueOf(UserId.get())).ifPresent(this::saveAuthentication);
                log.info(">> >> >> 유저 아이디 디비에서 찾음. saveAuthentication 했음.");
            } else{
                log.info(">> >> >> 유저 아이디 디비에서 찾을 수가 없음.");
            }
        }

        filterChain.doFilter(request, response);    //해당 유저의 인증 처리를 한다.
    }

    /**
     * [인증 허가 메소드]
     */
    public void saveAuthentication(User myUser) {
        log.info(">> JwtAuthenticationProcessingFilter.saveAuthentication() 호출");
        log.info(">> >> myUser : {}", myUser);
        String password = myUser.getPassword(); //파라미터로 들어온 유저의 password
        if (password == null) { // 소셜 로그인 유저의 비밀번호 임의로 설정 하여 소셜 로그인 유저도 인증 되도록 설정
            password = PasswordUtil.generateRandomPassword();   //소셜 로그인으로 하여 password가 없으면 임의로 생성 시켜놓는다
        }

        UserDetails userDetailsUser = org.springframework.security.core.userdetails.User.builder()  //사용자 인증 정보 생성
                .username(myUser.getEmail())    //유저이름은 사용자 이메일
                .password(password) //password는 위에서 설정한 password
                .roles(myUser.getRole().name()) //role은
                .build();

        Authentication authentication = // 인증 정보를 생성한다.
                new UsernamePasswordAuthenticationToken(userDetailsUser, null,
                        authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()));

        /*
        * 해당 유저의 객체를 SecurityContextHolder에 담아 인증 처리를 진행함.
        * */
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
