package com.codelabs.admin.catalago.infrastructure.web.in.genre.dto;

import com.codelabs.admin.catalago.domain.genre.Genre;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record GenreListResponse(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("is_active") Boolean active,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("deleted_at") Instant deletedAt
) {

    public static GenreListResponse from(final Genre genre) {
        return new GenreListResponse(
                genre.getId().getValue(),
                genre.getName(),
                genre.isActive(),
                genre.getCreatedAt(),
                genre.getDeletedAt()
        );
    }

}
