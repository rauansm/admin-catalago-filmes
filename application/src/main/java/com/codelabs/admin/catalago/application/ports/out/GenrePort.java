package com.codelabs.admin.catalago.application.ports.out;

import com.codelabs.admin.catalago.domain.genre.Genre;
import com.codelabs.admin.catalago.domain.genre.GenreID;
import com.codelabs.admin.catalago.domain.pagination.Pagination;
import com.codelabs.admin.catalago.domain.pagination.SearchQuery;

public interface GenrePort {
    Genre save(final Genre genre);

    Genre getById(final GenreID id);

    void deleteById(final GenreID id);

    Pagination<Genre> listGenres(final SearchQuery query);

}
