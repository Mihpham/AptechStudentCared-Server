package com.example.aptechstudentcaredserver.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dwvqposfl",
                "api_key", "631431656881998",
                "api_secret", "bHZCVw1p2yyHCvQ2y4uWFKFza0g",
                "secure", true
        ));
    }
}

