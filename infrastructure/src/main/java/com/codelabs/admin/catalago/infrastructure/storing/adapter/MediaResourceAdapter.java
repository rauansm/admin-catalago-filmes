package com.codelabs.admin.catalago.infrastructure.storing.adapter;

import com.codelabs.admin.catalago.application.ports.out.MediaResourcePort;
import com.codelabs.admin.catalago.common.exceptions.NotFoundException;
import com.codelabs.admin.catalago.common.stereotype.StoringAdapter;
import com.codelabs.admin.catalago.domain.enums.VideoMediaType;
import com.codelabs.admin.catalago.domain.video.*;
import com.codelabs.admin.catalago.infrastructure.storing.config.StoringProperties;
import com.codelabs.admin.catalago.infrastructure.storing.service.S3UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@StoringAdapter
@RequiredArgsConstructor
@Slf4j
@EnableConfigurationProperties(StoringProperties.class)
public class MediaResourceAdapter implements MediaResourcePort {

    private final StoringProperties properties;
    private final S3UploadService s3UploadService;

    @Override
    public AudioVideoMedia storeAudioVideo(VideoID id, VideoResource videoResource) {
        final var key = generateKey(id, videoResource.type());
        final var resource = videoResource.resource();
        store(key, resource);
        return AudioVideoMedia.with(resource.getChecksum(), resource.getName(), key);
    }

    @Override
    public ImageMedia storeImage(VideoID id, VideoResource videoResource) {
        final var key = generateKey(id, videoResource.type());
        final var resource = videoResource.resource();
        store(key, resource);
        return ImageMedia.with(resource.getChecksum(), resource.getName(), key);
    }

    @Override
    public Resource getResource(final VideoID id, final VideoMediaType type) {
        return this.s3UploadService.get(generateKey(id, type))
                .orElseThrow(() -> new NotFoundException("Resource %s not found for video %s".formatted(type, id)));
    }

    @Override
    public void clearResources(VideoID id) {
        final var ids = this.s3UploadService.list(folder(id));
        this.s3UploadService.deleteAll(ids);
    }

    private String generateKey(final VideoID id, final VideoMediaType type) {
        return folder(id)
                .concat("/")
                .concat(filename(type));
    }

    private String filename(final VideoMediaType type) {
        return properties.getFilenamePattern().replace("{type}", type.name());
    }

    private String folder(final VideoID id) {
        return properties.getLocationPattern().replace("{videoId}", id.getValue());
    }

    private void store(final String key, final Resource resource) {
        this.s3UploadService.store(key, resource);
    }
}
