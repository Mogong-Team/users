package com.login.OAuth2.study.dto;

import com.login.OAuth2.domain.user.users.User;
import com.login.OAuth2.study.StudyMethod;
import com.login.OAuth2.study.StudyType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class StudyResponseDto {

    private User leader;
    private StudyType studyType;
    private StudyMethod studyMethod;
    private String title;
    private String introduction;
    private String goal;
    private String recruitCondition;
    private LocalDateTime createDate;
    private LocalDateTime deadline;
}
