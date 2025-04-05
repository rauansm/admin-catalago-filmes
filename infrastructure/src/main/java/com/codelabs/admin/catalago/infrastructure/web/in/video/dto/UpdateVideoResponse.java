package com.codelabs.admin.catalago.infrastructure.web.in.video.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateVideoResponse(@JsonProperty("id") String id) {
}
