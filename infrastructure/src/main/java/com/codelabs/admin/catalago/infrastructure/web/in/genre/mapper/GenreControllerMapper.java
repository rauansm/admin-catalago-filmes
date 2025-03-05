package com.codelabs.admin.catalago.infrastructure.web.in.genre.mapper;

import com.codelabs.admin.catalago.domain.category.CategoryID;
import com.codelabs.admin.catalago.domain.genre.Genre;
import com.codelabs.admin.catalago.infrastructure.web.in.genre.dto.GenreRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

import static com.codelabs.admin.catalago.domain.genre.Genre.newGenre;

@Component
public class GenreControllerMapper {

    public Genre toDomain(final GenreRequest request) {
        Genre genre = newGenre(request.name(), request.isActive());
        genre.addCategories(toCategoryID(request.categories()));
        return genre;
    }

    public Genre toDomain(final GenreRequest request, final String id) {
        Genre genre = newGenre(id, request.name(), request.isActive());
        genre.addCategories(toCategoryID(request.categories()));
        return genre;
    }

    public <T> T toResponse(final Genre genre, Function<Genre, T> mapper) {
        return mapper.apply(genre);
    }

    private List<CategoryID> toCategoryID(final List<String> categories) {
        return categories.stream()
                .map(CategoryID::from)
                .toList();
    }
}


