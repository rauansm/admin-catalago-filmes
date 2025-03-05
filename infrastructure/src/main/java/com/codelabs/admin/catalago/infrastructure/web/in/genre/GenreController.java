package com.codelabs.admin.catalago.infrastructure.web.in.genre;

import com.codelabs.admin.catalago.application.ports.in.GenreUseCase;
import com.codelabs.admin.catalago.common.exceptions.PhysicalValidationException;
import com.codelabs.admin.catalago.domain.genre.Genre;
import com.codelabs.admin.catalago.domain.pagination.Pagination;
import com.codelabs.admin.catalago.domain.pagination.SearchQuery;
import com.codelabs.admin.catalago.infrastructure.web.in.genre.dto.GenreDetailsResponse;
import com.codelabs.admin.catalago.infrastructure.web.in.genre.dto.GenreListResponse;
import com.codelabs.admin.catalago.infrastructure.web.in.genre.dto.GenreRequest;
import com.codelabs.admin.catalago.infrastructure.web.in.genre.dto.GenreResponse;
import com.codelabs.admin.catalago.infrastructure.web.in.genre.mapper.GenreControllerMapper;
import com.codelabs.admin.catalago.infrastructure.web.in.genre.validator.GenreValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static net.logstash.logback.marker.Markers.append;

@RestController
@RequiredArgsConstructor
@Slf4j
public class GenreController implements GenreAPI {

    private static final String GENRE_FIELD = "genre";
    public static final String GENRE_WAS_MAPPED_SUCCESSFULLY = "Genre was mapped successfully";
    private final GenreUseCase genreUseCase;
    private final GenreControllerMapper mapper;
    private final GenreValidator validator;

    @Override
    public ResponseEntity<GenreResponse> create(final GenreRequest request) {
        log.info(append("body", request), "Receiving genre creation request");

        validator.validate(request).isInvalidThrow(PhysicalValidationException.class);

        final Genre genre = mapper.toDomain(request);
        log.info(append(GENRE_FIELD, genre), GENRE_WAS_MAPPED_SUCCESSFULLY);

        final Genre createdGenre = genreUseCase.create(genre);
        log.info(append(GENRE_FIELD, createdGenre), "Genre was created successfully");

        final GenreResponse genreResponse = mapper.toResponse(createdGenre, GenreResponse::from);
        log.info(append(GENRE_FIELD, genreResponse), "Created genre was mapped successfully");

        return ResponseEntity.created(URI.create("/genres/" + genreResponse.id()))
                .body(genreResponse);
    }

    @Override
    public Pagination<GenreListResponse> listGenres(final String search, final int page, final int perPage,
                                                    final String sort, final String direction) {
        log.info(append("search", search)
                        .and(append("page", page)
                                .and(append("perPage", perPage)
                                        .and(append("sort", sort)
                                                .and(append("dir", direction))))),
                "Receiving request to list genres by parameters");

        final Pagination<Genre> genrePagination = genreUseCase.listGenres(new SearchQuery(page, perPage, search, sort, direction));
        log.info(append(GENRE_FIELD, genrePagination), "genres found");

        final Pagination<GenreListResponse> listResponse = genrePagination.map(GenreListResponse::from);
        log.info(append(GENRE_FIELD, listResponse), "genres successfully mapped");

        return listResponse;
    }

    @Override
    public GenreDetailsResponse getById(final String id) {
        log.info("Receiving genre search request {}", id);

        final Genre genre = genreUseCase.getById(id);
        log.info(append(GENRE_FIELD, genre), "Genre found");

        final GenreDetailsResponse detailsResponse = mapper.toResponse(genre, GenreDetailsResponse::from);
        log.info(append(GENRE_FIELD, detailsResponse), GENRE_WAS_MAPPED_SUCCESSFULLY);

        return detailsResponse;
    }

    @Override
    public ResponseEntity<GenreResponse> updateById(final String id, final GenreRequest request) {
        log.info(append("id", id).and(append("body", request)), "Receiving genre update request");

        validator.validate(request).isInvalidThrow(PhysicalValidationException.class);

        final Genre genre = mapper.toDomain(request, id);
        log.info(append(GENRE_FIELD, genre), GENRE_WAS_MAPPED_SUCCESSFULLY);

        final Genre updatedGenre = genreUseCase.update(genre);
        log.info(append(GENRE_FIELD, genre), "Genre was update successfully");

        final GenreResponse genreResponse = mapper.toResponse(updatedGenre, GenreResponse::from);
        log.info(append(GENRE_FIELD, genreResponse), "Updated genre was mapped successfully");

        return ResponseEntity.ok(genreResponse);
    }

    @Override
    public void deleteById(final String id) {
        log.info("Receiving genre deletion request {}", id);

        genreUseCase.deleteById(id);

        log.info("deleted genre successfully");
    }

}
