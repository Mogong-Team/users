package com.login.OAuth2.global.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.login.OAuth2.domain.user.repository.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Transactional(readOnly = true) //읽기 전용임. 하위에 있는 모든 메서드 읽기. 만약 update
@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class JwtService {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;

    @Value("${jwt.access.header}")
    private String accessHeader;

    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    /**
     * JWT의 Subject와 Claim으로 id 사용 -> 클레임의 name을 "id"으로 설정
     * JWT의 헤더에 들어오는 값 : 'Authorization(Key) = Bearer {토큰} (Value)' 형식
     */
    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String ID_CLAIM = "userId";
    private static final String BEARER = "Bearer ";

    private final UserRepository userRepository;
    private final TokenBlackListService tokenBlackListService;

    /**
     * AccessToken 생성 메소드
     */
    public String createAccessToken(Long userId) {
        log.info(">> JwtService.createAccessToken() 실행 - jwt accessToken 생성");
        Date now = new Date();
        return JWT.create() // JWT 토큰을 생성하는 빌더 반환
                .withSubject(ACCESS_TOKEN_SUBJECT) // JWT의 Subject 지정 -> AccessToken이므로 AccessToken
                .withExpiresAt(new Date(now.getTime() + accessTokenExpirationPeriod)) // 토큰 만료 시간 설정

                //클레임으로 userId 하나만 사용
                .withClaim(ID_CLAIM, userId)
                .sign(Algorithm.HMAC512(secretKey)); // HMAC512 알고리즘 사용, application-jwt.yml에서 지정한 secret 키로 암호화
    }

    /**
     * RefreshToken 생성
     * RefreshToken은 Claim에 id도 넣지 않으므로 withClaim() X
     */
    public String createRefreshToken() {
        log.info(">> JwtService.createRefreshToken() 실행 - jwt refreshToken 생성");
        Date now = new Date();
        return JWT.create()
                .withSubject(REFRESH_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime() + refreshTokenExpirationPeriod))
                .sign(Algorithm.HMAC512(secretKey));
    }

    /**
     * AccessToken 헤더에 실어서 보내기
     */
    public void sendAccessToken(HttpServletResponse response, String accessToken) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader(accessHeader, accessToken);
        log.info("재발급된 Access Token : {}", accessToken);
    }

    /**
     * AccessToken + RefreshToken 헤더에 실어서 보내기
     */
    public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) {
        log.info(">> JwtService.sendAccessAndRefreshToken() 실행 - 액토 + 리토 응답 헤더에 붙임");
        response.setStatus(HttpServletResponse.SC_OK);
        setAccessTokenHeader(response, accessToken);
        setRefreshTokenHeader(response, refreshToken);
        log.info(">> >> Access Token, Refresh Token 헤더 설정 완료");
    }

    /**
     * 헤더에서 RefreshToken 추출
     * 토큰 형식 : Bearer XXX에서 Bearer를 제외하고 순수 토큰만 가져오기 위해서
     * 헤더를 가져온 후 "Bearer"를 삭제(""로 replace)
     */
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        log.info(">> JwtService.extractRefreshToken() 실행 - jwt 리토 요청 헤더에서 추출");
        return Optional.ofNullable(request.getHeader(refreshHeader))
                .filter(refreshToken -> refreshToken.startsWith(BEARER))
                .map(refreshToken -> refreshToken.replace(BEARER, ""));
    }

    /**
     * 헤더에서 AccessToken 추출
     * 토큰 형식 : Bearer XXX에서 Bearer를 제외하고 순수 토큰만 가져오기 위해서
     * 헤더를 가져온 후 "Bearer"를 삭제(""로 replace)
     */
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        log.info(">> JwtService.extractAccessToken() 실행 - jwt 액토 요청 헤더에서 추출");
        return Optional.ofNullable(request.getHeader(accessHeader))
                .filter(refreshToken -> refreshToken.startsWith(BEARER))
                .map(refreshToken -> refreshToken.replace(BEARER, ""));
    }

    /**
     * AccessToken에서 유저 아이디 추출
     * 추출 전에 JWT.require()로 검증기 생성
     * verify로 AceessToken 검증 후
     * 유효하다면 getClaim()으로 이메일 추출
     * 유효하지 않다면 빈 Optional 객체 반환
     */
    public Optional<Long> extractUserId(String accessToken) {
        log.info(">> JwtService.extractUserId() 실행 - jwt 액토에서 유저의 id 추출");

        if(accessToken == null){
            log.error("AccessToken Not Found");
        }

        try {
            // 토큰 유효성 검사하는 데에 사용할 알고리즘이 있는 JWT verifier builder 반환
            return Optional.ofNullable(JWT.require(Algorithm.HMAC512(secretKey))
                    .build() // 반환된 빌더로 JWT verifier 생성
                    .verify(accessToken) // accessToken을 검증하고 유효하지 않다면 예외 발생
                    .getClaim(ID_CLAIM) // claim(userId) 가져오기
                    .asLong());
        } catch (Exception e) {
            log.error("액세스 토큰이 유효하지 않습니다.");
            return Optional.empty();
        }
    }

    /**
     * AccessToken 헤더 설정
     */
    public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
        log.info(">> JwtService.setAccessTokenHeader() 실행 - jwt 응답 헤더에 액토 설정");
        response.setHeader(accessHeader, accessToken);
    }

    /**
     * RefreshToken 헤더 설정
     */
    public void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
        log.info(">> JwtService.setRefreshTokenHeader() 실행 - jwt 응답 헤더에 리토 설정");
        response.setHeader(refreshHeader, refreshToken);
    }
//
    /**
     * RefreshToken DB 저장(업데이트)
     */
    @Transactional(readOnly = false)
    public void updateRefreshToken(Long id, String refreshToken) {
        log.info(">> JwtService.updateRefreshToken() 실행 - jwt 리토 유저 DB에 저장");
        userRepository.findById(id)
                .ifPresentOrElse(
                        user -> user.updateRefreshToken(refreshToken),
                        () -> new Exception("일치하는 회원이 없습니다.")
                );
    }

    public boolean isTokenValid(String token) {
        log.info(">> JwtService.isTokenValid() 실행 - jwt 토큰 검사");

        if(tokenBlackListService.isTokenBlacklisted(token)){
            log.error(">> >> 토큰 블랙리스트에 들어있음");
            return false;
        }

        try {
            JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
            return true;
        } catch (Exception e) {
            log.error("유효하지 않은 토큰입니다. {}", e.getMessage());
            return false;
        }
    }

    public String expireAccessToken(String accessToken){
        log.info(">> JwtService.expireAccessToken() 호출 - 액토 만료시키기");

        try{
            DecodedJWT jwt = JWT.decode(accessToken);

            long expiresInMs = jwt.getExpiresAt().getTime() - System.currentTimeMillis();
            long expiresInMinutes = TimeUnit.MILLISECONDS.toMinutes(expiresInMs);

            long newExpirationTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1);

            return JWT.create()
                    .withSubject(ACCESS_TOKEN_SUBJECT)
                    .withExpiresAt(new Date(newExpirationTime))
                    .withClaim(ID_CLAIM, jwt.getClaim(ID_CLAIM).asString())
                    .sign(Algorithm.HMAC512(secretKey));
        } catch (Exception e){
            log.error("Failed to expire the access token: {}", e.getMessage());
            return null;
        }
    }
}
