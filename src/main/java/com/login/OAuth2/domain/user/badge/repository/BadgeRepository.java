package com.login.OAuth2.domain.user.badge.repository;

import com.login.OAuth2.domain.user.badge.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BadgeRepository extends JpaRepository<Badge, Long> {

    Optional<Badge> findByName(String name);
}
