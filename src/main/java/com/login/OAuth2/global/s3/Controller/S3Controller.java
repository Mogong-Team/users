package com.login.OAuth2.global.s3.Controller;

import com.login.OAuth2.global.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("imageFile") MultipartFile file){

        try{
            String imageUrl = s3Service.upload(file);
            log.info("imageUrl : " + imageUrl);

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.add("image-url", imageUrl);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .headers(responseHeaders)
                    .build();
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
