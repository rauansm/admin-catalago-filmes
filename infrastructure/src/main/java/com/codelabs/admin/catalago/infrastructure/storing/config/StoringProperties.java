package com.codelabs.admin.catalago.infrastructure.storing.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = "storage.catalogo-videos")
public class StoringProperties {

    private String locationPattern;

    private String filenamePattern;

    private String bucketName;
}
