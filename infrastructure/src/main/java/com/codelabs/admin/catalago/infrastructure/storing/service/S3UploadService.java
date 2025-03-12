package com.codelabs.admin.catalago.infrastructure.storing.service;

import com.codelabs.admin.catalago.domain.video.Resource;
import com.codelabs.admin.catalago.infrastructure.storing.config.StoringProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(StoringProperties.class)
public class S3UploadService implements StorageService {

    private final S3Client s3Client;
    private final StoringProperties properties;

    @Override
    public void store(final String key, final Resource resource) {
        final var putObjectRequest = PutObjectRequest.builder()
                .checksumCRC32C(resource.getChecksum())
                .bucket(properties.getBucketName())
                .key(key)
                .contentType(resource.getContentType())
                .contentLength(resource.getContentLength())
                .metadata(Map.of("name", resource.getName()))
                .build();

        final var requestBody = RequestBody.fromInputStream(resource.getInputStream(),
                resource.getContentLength());

        this.s3Client.putObject(putObjectRequest, requestBody);
    }

    @Override
    public Optional<Resource> get(String key) {

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(properties.getBucketName())
                .key(key)
                .checksumMode(ChecksumMode.ENABLED)
                .build();

        return Optional.ofNullable(s3Client.getObject(getObjectRequest))
                .map(responseStream -> {
                    GetObjectResponse response = responseStream.response();
                    String name = response.metadata().get("name");
                    return Resource.with(
                            responseStream,
                            response.contentLength(),
                            response.checksumCRC32C(),
                            response.contentType(),
                            name);
                });
    }

    @Override
    public List<String> list(String prefix) {
        ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(properties.getBucketName())
                .prefix(prefix)
                .build();

        ListObjectsV2Response response = s3Client.listObjectsV2(listRequest);

        return response.contents().stream()
                .map(S3Object::key)
                .collect(Collectors.toList());

    }

    @Override
    public void deleteAll(List<String> keys) {
        final var deleteRequests = keys.stream()
                .map(key -> DeleteObjectRequest.builder()
                        .bucket(properties.getBucketName())
                        .key(key)
                        .build())
                .toList();

        deleteRequests.forEach(s3Client::deleteObject);
    }
}
