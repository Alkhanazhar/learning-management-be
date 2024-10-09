package com.affy.learningManagementSystem.configurations;

import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = ObjectUtils.asMap(
                "cloud_name", "dupw6rdpc",
                "api_key", "255914797359629",
                "api_secret", "kw0X6Ngjy3EXYP3PKJ9j7hsRGBw",
                "secure", true
        );
        return new Cloudinary(config);
    }
}
