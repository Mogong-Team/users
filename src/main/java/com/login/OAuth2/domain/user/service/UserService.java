package com.login.OAuth2.domain.user.service;

import com.login.OAuth2.domain.user.Role;
import com.login.OAuth2.domain.user.User;
import com.login.OAuth2.domain.user.dto.UserSignUpDto;
import com.login.OAuth2.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void signUp(UserSignUpDto userSignUpDto) throws Exception {

        if (userRepository.findByEmail(userSignUpDto.getEmail()).isPresent()) {
            throw new Exception("이미 존재하는 이메일입니다.");
        }

        if (userRepository.findByNickname(userSignUpDto.getNickname()).isPresent()) {
            throw new Exception("이미 존재하는 닉네임입니다.");
        }

        User user = User.builder()
                .email(userSignUpDto.getEmail())
                .password(userSignUpDto.getPassword())
                .nickname(userSignUpDto.getNickname())
                .role(Role.USER)
                .build();

        //pwd를 먼저 빌드하고 후에 유저의 passwordEncode 메서드로 pwd를 암호화한다.
        user.passwordEncode(passwordEncoder);
        userRepository.save(user);

    }

    public User findUser(Long id){
        System.out.println(">> UserService.findUser() 호출 - 유저 찾기");
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + id + "Not Found"));
    }

    public void updateNickname(User user, String nickname){
        System.out.println(">> UserService.updateNickname() 호출 - 닉네임 수정");
        user.updateNickname(nickname);

        if(user.getRole() == Role.GUEST){
            user.authorizeUser();
        }
        userRepository.save(user);
    }

    public boolean isNicknameExists(String nickname){
        return userRepository.findByNickname(nickname).isPresent();
    }

//    public void updateRefreshToken(String email, String refreshToken) {
//        System.out.println(">> UserService.updateRefreshToken() 실행 - jwt 리토 유저 DB에 저장");
//        userRepository.findByEmail(email)
//                .ifPresentOrElse(
//                        user -> user.updateRefreshToken(refreshToken),
//                        () -> new Exception("일치하는 회원이 없습니다.")
//                );
//    }
}
