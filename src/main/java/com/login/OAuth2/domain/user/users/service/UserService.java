package com.login.OAuth2.domain.user.users.service;

import com.login.OAuth2.domain.user.users.Role;
import com.login.OAuth2.domain.user.users.User;
import com.login.OAuth2.domain.user.users.dto.UserSignUpDto;
import com.login.OAuth2.domain.user.users.repository.UserRepository;
import com.login.OAuth2.domain.user.util.ProfileImageUtil;
import com.login.OAuth2.global.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final S3Service s3Service;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
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
        log.info(">> UserService.findUser() 호출 - 유저 찾기");
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + id + "Not Found"));
    }

    @Transactional
    public void changeNickname(User user, String nickname){
        log.info(">> UserService.updateNickname() 호출 - 닉네임 수정");
        user.updateNickname(nickname);

        if(user.getRole() == Role.GUEST){
            user.authorizeUser();
        }
        userRepository.save(user);
    }

    public boolean isNicknameExists(String nickname){
        return userRepository.findByNickname(nickname).isPresent();
    }

    @Transactional
    public void changeImageUrl(User user, String imageUrl){

        String oldImageUrl = user.getImageUrl();

        if(!ProfileImageUtil.PROFILE_IMAGE_URLS.contains(oldImageUrl)){
            s3Service.delete(oldImageUrl);
        }

        user.updateImageUrl(imageUrl);
        userRepository.save(user);
    }
}
