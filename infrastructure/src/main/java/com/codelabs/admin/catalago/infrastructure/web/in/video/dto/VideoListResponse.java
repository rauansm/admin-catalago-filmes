package com.codelabs.admin.catalago.infrastructure.web.in.video.dto;

import com.codelabs.admin.catalago.domain.video.VideoPreview;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record VideoListResponse(
        @JsonProperty("id") String id,
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt
) {

  public static VideoListResponse from(final VideoPreview videoPreview) {
        return new VideoListResponse(
                videoPreview.id(),
                videoPreview.title(),
                videoPreview.description(),
                videoPreview.createdAt(),
                videoPreview.updatedAt()
        );
    }
}
