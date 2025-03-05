package com.codelabs.admin.catalago.infrastructure.web.in.category.dto;

import com.codelabs.admin.catalago.domain.category.Category;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record CategoryDetailsResponse(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("is_active") Boolean active,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt,
        @JsonProperty("deleted_at") Instant deletedAt
) {

    public static CategoryDetailsResponse from(final Category category) {
        return new CategoryDetailsResponse(
                category.getId().getValue(),
                category.getName(),
                category.getDescription(),
                category.isActive(),
                category.getCreatedAt(),
                category.getUpdatedAt(),
                category.getDeletedAt()
        );
    }

}
