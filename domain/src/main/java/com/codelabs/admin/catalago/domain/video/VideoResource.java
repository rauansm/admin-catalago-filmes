package com.codelabs.admin.catalago.domain.video;

import com.codelabs.admin.catalago.domain.enums.VideoMediaType;

public record VideoResource(
        VideoMediaType type,
        Resource resource
) {

    public static VideoResource with(final VideoMediaType type, final Resource resource) {
        return new VideoResource(type, resource);
    }
}
