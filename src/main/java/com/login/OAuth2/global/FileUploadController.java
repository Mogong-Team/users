package com.login.OAuth2.global;

import com.login.OAuth2.global.s3.service.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class FileUploadController {

    private final S3Uploader s3Uploader;

    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam("imageFile") MultipartFile file){

        try{
            String imageUrl = s3Uploader.upload(file);

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
