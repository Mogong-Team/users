package com.login.OAuth2.domain.user.users;

import com.login.OAuth2.domain.user.badge.Badge;
import com.login.OAuth2.study.Study;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
@Table(name = "USERS")
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String email; // 이메일
    private String password; // 비밀번호

    @Column(unique = true)
    private String nickname; // 닉네임

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private SocialType socialType; // KAKAO, NAVER, GOOGLE

    private String socialId; // 로그인한 소셜 타입의 식별자 값 (일반 로그인인 경우 null)

    private String refreshToken; // 리프레시 토큰

    @Builder.Default
    @ManyToMany
    @JoinTable(name = "user_badge", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "badge_id"))
    private Set<Badge> badges = new HashSet<>();

    @Builder.Default
    @ManyToMany
    @JoinTable(name = "my_study", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "study_id"))
    private Set<Study> myStudies = new HashSet<>();

    @Builder.Default
    @ManyToMany
    @JoinTable(name = "scrap_study", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "study_id"))
    private Set<Study> scrapStudies = new HashSet<>();



    // 유저 권한 설정 메소드
    public void authorizeUser() {
        System.out.println(">> User.authorizeUser() 호출 - role, User로 변경");
        this.role = Role.USER;
    }

    // 비밀번호 암호화 메소드
    public void passwordEncode(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    public void updateRefreshToken(String updateRefreshToken) {
        this.refreshToken = updateRefreshToken;
    }

    public void updateNickname(String updateNickname){
        this.nickname = updateNickname;
    }

    public void updateImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }
}
