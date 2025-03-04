package com.codelabs.admin.catalago.infrastructure.web.in.genre.dto;

import com.codelabs.admin.catalago.domain.genre.Genre;

public record GenreResponse(
        String id
) {
    public static GenreResponse from(final String id) {
        return new GenreResponse(id);
    }

    public static GenreResponse from(final Genre genre) {
        return new GenreResponse(genre.getId().getValue());
    }
}
