package com.codelabs.admin.catalago.e2e;

import com.codelabs.admin.catalago.domain.Identifier;
import com.codelabs.admin.catalago.domain.category.CategoryID;
import com.codelabs.admin.catalago.domain.genre.GenreID;
import com.codelabs.admin.catalago.infrastructure.web.config.json.Json;
import com.codelabs.admin.catalago.infrastructure.web.in.category.dto.CategoryDetailsResponse;
import com.codelabs.admin.catalago.infrastructure.web.in.category.dto.CategoryRequest;
import com.codelabs.admin.catalago.infrastructure.web.in.genre.dto.GenreDetailsResponse;
import com.codelabs.admin.catalago.infrastructure.web.in.genre.dto.GenreRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.function.Function;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public interface MockDsl {

    MockMvc mvc();

    default ResultActions deleteACategory(final Identifier id) throws Exception {
        return this.delete("/categories/", id);
    }

    default CategoryID givenACategory(final String name, final String description, final boolean isActive) throws Exception {
        final var requestBody = new CategoryRequest(name, description, isActive);
        final var actualId = this.given("/categories", requestBody);
        return CategoryID.from(actualId);
    }

    default ResultActions listCategories(final int page, final int perPage) throws Exception {
        return listCategories(page, perPage, "", "", "");
    }

    default ResultActions listCategories(final int page, final int perPage, final String search) throws Exception {
        return listCategories(page, perPage, search, "", "");
    }

    default ResultActions listCategories(final int page, final int perPage, final String search, final String sort, final String direction) throws Exception {
        return this.list("/categories", page, perPage, search, sort, direction);
    }

    default CategoryDetailsResponse retrieveACategory(final Identifier id) throws Exception {
        return this.retrieve("/categories/", id, CategoryDetailsResponse.class);
    }

    default ResultActions updateACategory(final Identifier id, final CategoryRequest request) throws Exception {
        return this.update("/categories/", id, request);
    }

    default ResultActions deleteAGenre(final Identifier id) throws Exception {
        return this.delete("/genres/", id);
    }

    default GenreID givenAGenre(final String name, final boolean isActive, final List<CategoryID> categories) throws Exception {
        final var requestBody = new GenreRequest(name, mapTo(categories, CategoryID::getValue), isActive);
        final var actualId = this.given("/genres", requestBody);
        return GenreID.from(actualId);
    }

    default ResultActions listGenres(final int page, final int perPage) throws Exception {
        return listGenres(page, perPage, "", "", "");
    }

    default ResultActions listGenres(final int page, final int perPage, final String search) throws Exception {
        return listGenres(page, perPage, search, "", "");
    }

    default ResultActions listGenres(final int page, final int perPage, final String search, final String sort, final String direction) throws Exception {
        return this.list("/genres", page, perPage, search, sort, direction);
    }

    default GenreDetailsResponse retrieveAGenre(final Identifier id) throws Exception {
        return this.retrieve("/genres/", id, GenreDetailsResponse.class);
    }

    default ResultActions updateAGenre(final Identifier id, final GenreRequest request) throws Exception {
        return this.update("/genres/", id, request);
    }

    default <A, D> List<D> mapTo(final List<A> actual, final Function<A, D> mapper) {
        return actual.stream()
                .map(mapper)
                .toList();
    }

    private String given(final String url, final Object body) throws Exception {
        final var request = post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(body));

        final var actualId = this.mvc().perform(request)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getHeader("Location")
                .replace("%s/".formatted(url), "");

        return actualId;
    }

    private ResultActions list(final String url, final int page, final int perPage, final String search, final String sort, final String direction) throws Exception {
        final var request = get(url)
                .queryParam("page", String.valueOf(page))
                .queryParam("perPage", String.valueOf(perPage))
                .queryParam("search", search)
                .queryParam("sort", sort)
                .queryParam("dir", direction)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        return this.mvc().perform(request);
    }

    private <T> T retrieve(final String url, final Identifier id, final Class<T> clazz) throws Exception {
        final var request = get(url + id.getValue())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8);

        final var json = this.mvc().perform(request)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        return Json.readValue(json, clazz);
    }

    private ResultActions delete(final String url, final Identifier id) throws Exception {
        final var request = MockMvcRequestBuilders.delete(url + id.getValue())
                .contentType(MediaType.APPLICATION_JSON);

        return this.mvc().perform(request);
    }

    private ResultActions update(final String url, final Identifier id, final Object requestBody) throws Exception {
        final var request = put(url + id.getValue())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(requestBody));

        return this.mvc().perform(request);
    }
}
