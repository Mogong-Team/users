package com.login.OAuth2.study.service;

import com.login.OAuth2.study.Study;
import com.login.OAuth2.study.dto.StudyDto;
import com.login.OAuth2.study.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StudyService {

    private final StudyRepository studyRepository;

    public void createStudy(StudyDto studyDto){

        Study study = toEntity(studyDto);
        studyRepository.save(study);
    }

    private Study toEntity(StudyDto studyDto){

        return Study.builder()
                .leader(studyDto.getLeader())
                .studyType(studyDto.getStudyType())
                .studyMethod(studyDto.getStudyMethod())
                .title(studyDto.getTitle())
                .introduction(studyDto.getIntroduction())
                .goal(studyDto.getGoal())
                .recruitCondition(studyDto.getRecruitCondition())
                .createDate(studyDto.getCreateDate())
                .deadline(studyDto.getDeadline())
                .build();
    }
}
