package com.login.OAuth2.domain.user.profile.repository;

import com.login.OAuth2.domain.user.profile.BasicProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasicProfileRepository extends JpaRepository<BasicProfile, Long> {

}
