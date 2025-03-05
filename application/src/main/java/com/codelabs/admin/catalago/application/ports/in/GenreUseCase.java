package com.codelabs.admin.catalago.application.ports.in;

import com.codelabs.admin.catalago.domain.genre.Genre;
import com.codelabs.admin.catalago.domain.pagination.Pagination;
import com.codelabs.admin.catalago.domain.pagination.SearchQuery;

public interface GenreUseCase {
    Genre create(final Genre genre);

    Genre update(final Genre genre);

    Genre getById(final String id);

    void deleteById(final String id);

    Pagination<Genre> listGenres(final SearchQuery query);
}
