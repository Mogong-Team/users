package com.login.OAuth2.domain.user.profile.service;

import com.login.OAuth2.domain.user.profile.BasicProfile;
import com.login.OAuth2.domain.user.profile.repository.BasicProfileRepository;
import com.login.OAuth2.domain.user.users.User;
import com.login.OAuth2.domain.user.users.repository.UserRepository;
import com.login.OAuth2.domain.user.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class BasicProfileService {

    private final BasicProfileRepository basicProfileRepository;
    private final UserService userService;

    public void setFirstBasicProfile(Long userId){

        List<BasicProfile> basicProfileList = basicProfileRepository.findAll();

        BasicProfile selectedBasicProfile = getRandomBasicProfile(basicProfileList);

        User user = userService.findUser(userId);

        userService.updateBasicProfile(user, selectedBasicProfile);
    }

    private BasicProfile getRandomBasicProfile(List<BasicProfile> basicProfileList){

        int randomIdx = new Random().nextInt(basicProfileList.size());
        return basicProfileList.get(randomIdx);
    }

}
