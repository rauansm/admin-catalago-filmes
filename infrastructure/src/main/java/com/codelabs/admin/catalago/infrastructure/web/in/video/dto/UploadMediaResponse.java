package com.codelabs.admin.catalago.infrastructure.web.in.video.dto;

import com.codelabs.admin.catalago.domain.enums.VideoMediaType;
import com.fasterxml.jackson.annotation.JsonProperty;

public record UploadMediaResponse(
        @JsonProperty("video_id") String videoId,
        @JsonProperty("media_type") VideoMediaType mediaType
) {
}
