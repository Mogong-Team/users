package com.login.OAuth2.global.s3.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class S3Service {

    private static String bucket = "mogong-s3-bucket";

    private final AmazonS3Client amazonS3Client;

    /**
     * S3 파일 업로드
     * */
    public String upload(MultipartFile multipartFile) throws IOException {

        String fileName = UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        objectMetadata.setContentLength(multipartFile.getSize());

        amazonS3Client.putObject(bucket, fileName, multipartFile.getInputStream(), objectMetadata);

        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    public void delete(String imageUrl){

        if(amazonS3Client.doesObjectExist(bucket, imageUrl))
        amazonS3Client.deleteObject(bucket, imageUrl);
    }
}


