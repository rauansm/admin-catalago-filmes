package com.codelabs.admin.catalago.infrastructure.web.in.genre.dto;

import com.codelabs.admin.catalago.domain.category.CategoryID;
import com.codelabs.admin.catalago.domain.genre.Genre;
import com.codelabs.admin.catalago.infrastructure.web.in.category.dto.CategoryDetailsResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

public record GenreDetailsResponse(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("categories_id") List<String> categories,
        @JsonProperty("is_active") Boolean active,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt,
        @JsonProperty("deleted_at") Instant deletedAt
) {

    public static GenreDetailsResponse from(final Genre genre) {
        return new GenreDetailsResponse(
                genre.getId().getValue(),
                genre.getName(),
                getCategories(genre),
                genre.isActive(),
                genre.getCreatedAt(),
                genre.getUpdatedAt(),
                genre.getDeletedAt()
        );
    }

    private static List<String> getCategories(Genre genre) {
        return genre.getCategories().stream()
                .map(CategoryID::getValue)
                .toList();
    }
}
