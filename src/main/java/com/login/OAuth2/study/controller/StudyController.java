package com.login.OAuth2.study.controller;


import com.amazonaws.Response;
import com.login.OAuth2.domain.user.users.service.UserService;
import com.login.OAuth2.global.jwt.service.JwtService;
import com.login.OAuth2.study.Study;
import com.login.OAuth2.study.dto.StudyDto;
import com.login.OAuth2.study.dto.StudyResponseDto;
import com.login.OAuth2.study.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@RequestMapping("/study")
@RestController
public class StudyController {

    private final StudyService studyService;
    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping("/")
    public ResponseEntity<StudyDto> createStudy(HttpServletRequest request, @RequestBody StudyDto studyDto){
        System.out.println("createStudy() 실행");

        System.out.println(studyDto);
        Long leaderId = jwtService.getUserId(request);

        System.out.println(leaderId);
        studyDto.setLeader(userService.findUser(leaderId));
        studyDto.setCreateDate(LocalDateTime.now());
        studyService.createStudy(studyDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(studyDto);
    }

    @GetMapping("/{studyId}")
    public ResponseEntity<StudyResponseDto> readStudy(HttpServletRequest request, @PathVariable Long studyId){

        Study study = studyService.findStudy(studyId);
        StudyResponseDto studyResponseDto = studyService.toResponseDto(study);

        return ResponseEntity.status(HttpStatus.OK).body(studyResponseDto);
    }
}
