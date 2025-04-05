package com.codelabs.admin.catalago.infrastructure.storing.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(S3Properties.class)
public class AmazonS3Config {

    private final S3Properties properties;

    @Bean
    public S3Client amazonS3() {
        var region = properties.getRegion();

//        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(
//                properties.getAccessKey(),
//                properties.getSecretKey()
//        );

        return S3Client.builder()
                .region(Region.of(region))
//                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }

}
