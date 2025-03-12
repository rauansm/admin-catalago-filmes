package com.codelabs.admin.catalago.infrastructure.storing.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = "aws.s3")
public class S3Properties {

    private String region;
    private String accessKey;
    private String secretKey;
}
