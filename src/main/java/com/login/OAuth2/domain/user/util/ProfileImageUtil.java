package com.login.OAuth2.domain.user.util;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ProfileImageUtil {

    public static final List<String> PROFILE_IMAGE_URLS = Arrays.asList(
            "https://mogong-s3-bucket.s3.ap-northeast-2.amazonaws.com/basicProfile1.png",
            "https://mogong-s3-bucket.s3.ap-northeast-2.amazonaws.com/basicProfile2.png",
            "https://mogong-s3-bucket.s3.ap-northeast-2.amazonaws.com/basicProfile3.png",
            "https://mogong-s3-bucket.s3.ap-northeast-2.amazonaws.com/basicProfile4.png"
    );

    public static String getRandomImageUrl(){
        return PROFILE_IMAGE_URLS.get(new Random().nextInt(PROFILE_IMAGE_URLS.size()));
    }
}
