package com.login.OAuth2.global.s3.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.login.OAuth2.domain.user.util.ProfileImageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3Service {

    private static String bucket = "mogong-s3-bucket";
    private final AmazonS3Client amazonS3Client;

    public String upload(MultipartFile multipartFile) throws IOException {

        String fileName = UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();
        log.info(">> >> s3.upload() : fileName = {}", fileName);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        objectMetadata.setContentLength(multipartFile.getSize());

        try(InputStream inputStream = multipartFile.getInputStream()){
            amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                    );
        } catch (Exception e){
            log.info(e.getMessage());
        }

        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    private String generateUniqueFileName(String originalFileName){
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        return UUID.randomUUID().toString() + extension;
    }

    public void delete(String imageUrl) {

        String objectKey = getObjectKeyFromUrl(imageUrl);
        objectKey = URLDecoder.decode(objectKey, StandardCharsets.UTF_8);

        if(!ProfileImageUtil.PROFILE_IMAGE_URLS.contains(imageUrl)) {
            try {
                amazonS3Client.deleteObject(bucket, objectKey);
            } catch (AmazonS3Exception e) {
                log.info("Failed file delete : {}", e.getMessage());
            }
        }
    }

    private String getObjectKeyFromUrl(String imageUrl){
        return imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
    }
}


