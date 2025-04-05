package com.codelabs.admin.catalago.application.service.genre;

import com.codelabs.admin.catalago.application.ports.in.GenreUseCase;
import com.codelabs.admin.catalago.application.ports.out.CategoryPort;
import com.codelabs.admin.catalago.application.ports.out.GenrePort;
import com.codelabs.admin.catalago.common.exceptions.NotFoundException;
import com.codelabs.admin.catalago.common.stereotype.UseCase;
import com.codelabs.admin.catalago.domain.category.CategoryID;
import com.codelabs.admin.catalago.domain.genre.Genre;
import com.codelabs.admin.catalago.domain.genre.GenreID;
import com.codelabs.admin.catalago.domain.pagination.Pagination;
import com.codelabs.admin.catalago.domain.pagination.SearchQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static net.logstash.logback.marker.Markers.append;

@UseCase
@RequiredArgsConstructor
@Slf4j
public class GenreService implements GenreUseCase {

    public static final String GENRE_FIELD = "genre";
    private final GenrePort genrePort;
    private final CategoryPort categoryPort;

    @Override
    public Genre create(final Genre genre) {
        log.info(append(GENRE_FIELD, genre), "Starting genre creation service");

        validateCategories(genre.getCategories());
        final var savedGenre = this.genrePort.save(genre);

        log.info("Genre creation service completed successfully.");
        return savedGenre;
    }

    @Override
    public Genre update(final Genre genre) {
        log.info(append(GENRE_FIELD, genre), "Starting genre update service");

        final var genreFound = this.genrePort.getById(genre.getId());
        validateCategories(genre.getCategories());

        genreFound.update(genre.getName(), genre.isActive(), genre.getCategories());
        log.info(append(GENRE_FIELD, genreFound), "Genre update successfully.");

        return genrePort.save(genreFound);
    }

    @Override
    public Genre getById(final String id) {
        log.info(append("id", id), "Search genre by id");

        final var genre = this.genrePort.getById(GenreID.from(id));
        log.info(append(GENRE_FIELD, genre), "Genre successfully found.");

        return genre;
    }

    @Override
    public void deleteById(final String id) {
        log.info(append("id", id), "delete genre by id");

        this.genrePort.deleteById(GenreID.from(id));

        log.info("Genre delete service completed successfully.");
    }

    @Override
    public Pagination<Genre> listGenres(final SearchQuery query) {
        log.info(append("query", query), "Search all genre by query params");

        final var genres = this.genrePort.listGenres(query);
        log.info(append("genres", genres), "Found genres");

        return genres;
    }

    private void validateCategories(final List<CategoryID> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        final var retrievedIds = categoryPort.existsByIds(ids);
        log.info("ids retrieved from database {}", ids);

        if (ids.size() != retrievedIds.size()) {
            final var missingIds = new ArrayList<>(ids);
            missingIds.removeAll(retrievedIds);

            final var missingIdsMessage = missingIds.stream()
                    .map(CategoryID::getValue)
                    .collect(Collectors.joining(", "));

            throw new NotFoundException("Some categories could not be found: %s".formatted(missingIdsMessage));
        }
    }

}
