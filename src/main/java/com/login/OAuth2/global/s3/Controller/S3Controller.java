package com.login.OAuth2.global.s3.Controller;

import com.login.OAuth2.global.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/s3")
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("imageFile") MultipartFile file){

        try{
            String imageUrl = s3Service.upload(file);
            log.info("S3Controller.uploadFile() : imageUrl = {}", imageUrl);

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

    @DeleteMapping
    public ResponseEntity<String> deleteFile(HttpServletRequest request){

        String imageUrl = request.getHeader("image-url");

        if(imageUrl != null && !imageUrl.isEmpty()){
            s3Service.delete(imageUrl);
            return ResponseEntity.status(HttpStatus.OK).body("Image delete successfully");
        } else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Image URL is missing");
        }
    }
}
