package com.codelabs.admin.catalago.infrastructure.web.in.video.dto;


import com.codelabs.admin.catalago.domain.video.Video;

public record VideoResponse(String id) {

    public static VideoResponse from(final Video video) {
        return new VideoResponse(video.getId().getValue());
    }
}
