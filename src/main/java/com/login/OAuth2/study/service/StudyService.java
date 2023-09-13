package com.login.OAuth2.study.service;

import com.login.OAuth2.study.Study;
import com.login.OAuth2.study.dto.StudyDto;
import com.login.OAuth2.study.dto.StudyResponseDto;
import com.login.OAuth2.study.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@RequiredArgsConstructor
@Service
public class StudyService {

    private final StudyRepository studyRepository;

    public void createStudy(StudyDto studyDto){

        Study study = toEntity(studyDto);
        studyRepository.save(study);
    }
    
    public Study findStudy(Long studyId){
        return studyRepository.findById(studyId)
                .orElseThrow(() -> new EntityNotFoundException());
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

    public StudyResponseDto toResponseDto(Study study){

        return StudyResponseDto.builder()
                .leader(study.getLeader())
                .studyType(study.getStudyType())
                .studyMethod(study.getStudyMethod())
                .title(study.getTitle())
                .introduction(study.getIntroduction())
                .goal(study.getGoal())
                .recruitCondition(study.getRecruitCondition())
                .createDate(study.getCreateDate())
                .deadline(study.getDeadline())
                .build();
    }
}
