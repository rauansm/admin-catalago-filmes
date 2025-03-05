package com.codelabs.admin.catalago.infrastructure.web.in.category.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CategoryRequest(
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("is_active") Boolean active
) {
}
