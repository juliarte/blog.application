package com.system.blog.application.aws;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "aws.s3.buckets")
@Setter
@Getter
public class S3Buckets {

    private String customer;//use from properties instead of hardcoding
    
}
