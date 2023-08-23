package com.login.OAuth2.domain.user.badge.service;

import com.login.OAuth2.domain.user.badge.Badge;
import com.login.OAuth2.domain.user.badge.repository.BadgeRepository;
import com.login.OAuth2.domain.user.users.User;
import com.login.OAuth2.domain.user.users.repository.UserRepository;
import com.login.OAuth2.domain.user.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BadgeService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final BadgeRepository badgeRepository;

    @Transactional
    public boolean takeBadge(Long userId, String badgeName) {

        User user = userService.findUser(userId);
        Optional<Badge> badgeOptional = badgeRepository.findByName(badgeName);

        if(badgeOptional.isPresent()){
            Badge badge = badgeOptional.get();

            user.getBadges().add(badge);
            badge.getUsers().add(user);

            userRepository.save(user);
            badgeRepository.save(badge);

            return true;
        } else{
            return false;
        }

    }
}
