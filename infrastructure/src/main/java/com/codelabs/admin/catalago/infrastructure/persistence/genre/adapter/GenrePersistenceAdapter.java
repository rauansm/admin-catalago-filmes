package com.codelabs.admin.catalago.infrastructure.persistence.genre.adapter;

import com.codelabs.admin.catalago.application.ports.out.GenrePort;
import com.codelabs.admin.catalago.common.exceptions.NotFoundException;
import com.codelabs.admin.catalago.common.stereotype.PersistenceAdapter;
import com.codelabs.admin.catalago.common.utils.SpecificationUtils;
import com.codelabs.admin.catalago.domain.genre.Genre;
import com.codelabs.admin.catalago.domain.genre.GenreID;
import com.codelabs.admin.catalago.domain.pagination.Pagination;
import com.codelabs.admin.catalago.domain.pagination.SearchQuery;
import com.codelabs.admin.catalago.infrastructure.persistence.genre.entity.GenreEntity;
import com.codelabs.admin.catalago.infrastructure.persistence.genre.repositoy.GenreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

import static net.logstash.logback.argument.StructuredArguments.kv;
import static net.logstash.logback.marker.Markers.append;
import static org.springframework.data.jpa.domain.Specification.where;

@PersistenceAdapter
@RequiredArgsConstructor
@Slf4j
public class GenrePersistenceAdapter implements GenrePort {

    private final GenreRepository genreRepository;

    @Override
    public Genre save(final Genre genre) {
        log.info(append("genre", genre), "Starting genre persistence in the database...");

        final GenreEntity entity = GenreEntity.from(genre);
        log.info(append("entity", entity), "Object mapped successfully!");

        final GenreEntity savedEntity = this.genreRepository.save(entity);

        log.info(append("entity", savedEntity), "Genre persisted successfully!");
        return savedEntity.toAggregate();
    }

    @Override
    public Genre getById(final GenreID id) {
        log.info("Searching genre in the database... {}", id.getValue());

        final Optional<GenreEntity> genreEntity = this.genreRepository.findById(id.getValue());

        genreEntity.ifPresent(
                entity -> log.info(append("entity", entity), "Genre found successfully!"));

        final Genre genre = genreEntity
                .map(GenreEntity::toAggregate)
                .orElseThrow(() -> new NotFoundException(String.format("Genre not found in database with id %s", id.getValue())));

        log.info(append("genre", genre), "Entity to domain mapping done!");
        return genre;
    }

    @Override
    public void deleteById(final GenreID id) {
        log.info("Starting genre deletion in the database... {}", id.getValue());

        final String idValue = id.getValue();
        if (this.genreRepository.existsById(idValue)) {
            this.genreRepository.deleteById(idValue);
            log.info("Genre deleted successfully! {}", idValue);
        }
    }

    @Override
    public Pagination<Genre> listGenres(final SearchQuery query) {
        log.info(append("params", query), "Searching genre in database by parameters");

        // Paginação
        final var page = PageRequest.of(
                query.page(),
                query.perPage(),
                Sort.by(Sort.Direction.fromString(query.direction()), query.sort())
        );

        // Busca dinamica pelo criterio terms (name)
        final var where = Optional.ofNullable(query.terms())
                .filter(str -> !str.isBlank())
                .map(this::assembleSpecification)
                .orElse(null);

        final var pageResult =
                this.genreRepository.findAll(where(where), page);
        log.info("were found {} genres", kv("genres_size", pageResult.getTotalElements()));

        return new Pagination<>(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.map(GenreEntity::toAggregate).toList()
        );
    }

    private Specification<GenreEntity> assembleSpecification(final String terms) {
        return SpecificationUtils.like("name", terms);
    }

}
