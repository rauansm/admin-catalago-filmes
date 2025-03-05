package com.codelabs.admin.catalago.infrastructure.web.in.genre;

import br.com.fluentvalidator.context.Error;
import br.com.fluentvalidator.context.ValidationResult;
import com.codelabs.admin.catalago.ControllerTest;
import com.codelabs.admin.catalago.application.ports.in.GenreUseCase;
import com.codelabs.admin.catalago.common.exceptions.NotFoundException;
import com.codelabs.admin.catalago.domain.category.CategoryID;
import com.codelabs.admin.catalago.domain.enums.ProblemType;
import com.codelabs.admin.catalago.domain.genre.Genre;
import com.codelabs.admin.catalago.domain.pagination.Pagination;
import com.codelabs.admin.catalago.infrastructure.web.in.genre.dto.GenreDetailsResponse;
import com.codelabs.admin.catalago.infrastructure.web.in.genre.dto.GenreRequest;
import com.codelabs.admin.catalago.infrastructure.web.in.genre.dto.GenreResponse;
import com.codelabs.admin.catalago.infrastructure.web.in.genre.mapper.GenreControllerMapper;
import com.codelabs.admin.catalago.infrastructure.web.in.genre.validator.GenreValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ControllerTest(controllers = GenreAPI.class)
public class GenreAPITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GenreUseCase useCase;

    @MockBean
    private GenreControllerMapper mapper;

    @MockBean
    private GenreValidator validator;

    @Test
    public void givenAValidDto_whenCallsCreateGenre_shouldReturnGenreId() throws Exception {
        // given
        final var expectedName = "Ação";
        final var expectedCategories = List.of(
                CategoryID.from("7b40921e-a2fb-4624-a543-205eb84eb2c1"),
                CategoryID.from("e1c51c36-66ae-4672-a3fd-3e9737ab82a3"));
        final var expectedIsActive = true;
        final var expectedId = "1e2f2d8f-dceb-4cec-8cc9-cc9ad42dd0eb";

        final var genre = Genre.newGenre(expectedId, expectedName, expectedIsActive).addCategories(expectedCategories);

        when(validator.validate(any(GenreRequest.class))).thenReturn(ValidationResult.ok());
        when(mapper.toDomain(any())).thenReturn(genre);
        when(useCase.create(any())).thenReturn(genre);
        when(mapper.toResponse(any(), any())).thenReturn(GenreResponse.from(expectedId));

        // when
        final var request = post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(genre));

        final var response = this.mvc.perform(request)
                .andDo(print());

        // then
        response.andExpect(status().isCreated())
                .andExpect(header().string("Location", "/genres/" + expectedId))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(expectedId)));

        verify(useCase).create(argThat(domain ->
                Objects.equals(expectedName, domain.getName())
                        && Objects.equals(expectedCategories, domain.getCategories())
                        && Objects.equals(expectedIsActive, domain.isActive())
        ));
    }

    @Test
    public void givenAnInvalidName_whenCallsValidator_shouldReturnPhysicalValidationException() throws Exception {
        // given
        final String expectedName = null;
        final var expectedCategories = List.of("123", "456");
        final var expectedIsActive = true;

        final var expectedField = "name";
        final var expectedCode = ProblemType.NAME_MUST_NOT_BE_NULL.name();
        final var expectedMessage = ProblemType.NAME_MUST_NOT_BE_NULL.getDescription();

        final var input = new GenreRequest(expectedName, expectedCategories, expectedIsActive);

        when(validator.validate(any(GenreRequest.class)))
                .thenReturn(ValidationResult.fail(List.of(Error.create(expectedField, expectedMessage, expectedCode, null))));

        // when
        final var request = post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(input));

        final var response = this.mvc.perform(request)
                .andDo(print());

        // then
        response.andExpect(status().isBadRequest())
                .andExpect(header().string("Location", nullValue()))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.fields", hasSize(1)))
                .andExpect(jsonPath("$.fields[0].field", equalTo(expectedField)))
                .andExpect(jsonPath("$.fields[0].code", equalTo(expectedCode)))
                .andExpect(jsonPath("$.fields[0].message", equalTo(expectedMessage)));

        verify(validator, times(1)).validate(any(GenreRequest.class));
    }

    @Test
    public void givenAValidId_whenCallsGetGenreById_shouldReturnGenre() throws Exception {
        // given
        final var expectedName = "Ação";
        final var expectedCategories = List.of(
                CategoryID.from("7b40921e-a2fb-4624-a543-205eb84eb2c1"),
                CategoryID.from("e1c51c36-66ae-4672-a3fd-3e9737ab82a3"));
        final var expectedIsActive = false;

        final var genre = Genre.newGenre(expectedName, expectedIsActive)
                .addCategories(expectedCategories);

        final var expectedId = genre.getId().getValue();

        when(useCase.getById(any()))
                .thenReturn(genre);

        when(mapper.toResponse(any(), any()))
                .thenReturn(GenreDetailsResponse.from(genre));

        // when
        final var request = get("/genres/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(request);

        // then
        response.andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(expectedId)))
                .andExpect(jsonPath("$.name", equalTo(expectedName)))
                .andExpect(jsonPath("$.categories_id", equalTo(expectedCategories.stream().map(CategoryID::getValue).toList())))
                .andExpect(jsonPath("$.is_active", equalTo(expectedIsActive)))
                .andExpect(jsonPath("$.created_at", equalTo(genre.getCreatedAt().toString())))
                .andExpect(jsonPath("$.updated_at", equalTo(genre.getUpdatedAt().toString())))
                .andExpect(jsonPath("$.deleted_at", equalTo(genre.getDeletedAt().toString())));

        verify(useCase).getById(eq(expectedId));
    }

    @Test
    public void givenAInvalidId_whenCallsGetGenre_shouldReturnNotFound() throws Exception {
        // given
        final var expectedId = "5f82365c-ab4c-42a3-aed4-2d103588e7b7";
        final var expectedErrorMessage = "Genre not found in database with id " + expectedId;

        when(useCase.getById(any()))
                .thenThrow(new NotFoundException("Genre not found in database with id " + expectedId));

        // when
        final var request = get("/genres/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(request)
                .andDo(print());

        // then
        response.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));
    }

    @Test
    public void givenAValidDto_whenCallsUpdateGenre_shouldReturnGenreId() throws Exception {
        // given
        final var expectedName = "Ação";
        final var expectedCategories = List.of("7b40921e-a2fb-4624-a543-205eb84eb2c1", "e1c51c36-66ae-4672-a3fd-3e9737ab82a3");
        final var expectedIsActive = true;

        final var genre = Genre.newGenre(expectedName, expectedIsActive)
                .addCategories(expectedCategories.stream().map(CategoryID::from).toList());
        final var expectedId = genre.getId().getValue();

        final var input = new GenreRequest(expectedName, expectedCategories, expectedIsActive);

        when(validator.validate(any(GenreRequest.class))).thenReturn(ValidationResult.ok());
        when(mapper.toDomain(any(), any())).thenReturn(genre);
        when(useCase.update(any())).thenReturn(genre);
        when(mapper.toResponse(any(), any())).thenReturn(GenreResponse.from(expectedId));

        // when
        final var request = put("/genres/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input));

        final var response = this.mvc.perform(request)
                .andDo(print());

        // then
        response.andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(expectedId)));

        verify(useCase, times(1)).update(argThat(categoryUpdate ->
                Objects.equals(expectedName, categoryUpdate.getName())
                        && Objects.equals(expectedCategories, categoryUpdate.getCategories().stream().map(CategoryID::getValue).toList())
                        && Objects.equals(expectedIsActive, categoryUpdate.isActive())
        ));
    }

    @Test
    public void givenAValidId_whenCallsDeleteGenre_shouldBeOK() throws Exception {
        // given
        final var expectedId = "7b40921e-a2fb-4624-a543-205eb84eb2c1";

        doNothing()
                .when(useCase).deleteById(any());

        // when
        final var request = delete("/genres/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON);

        final var result = this.mvc.perform(request);

        // then
        result.andExpect(status().isNoContent());

        verify(useCase).deleteById(eq(expectedId));
    }

    @Test
    public void givenValidParams_whenCallsListGenres_shouldReturnGenres() throws Exception {
        // given
        final var genre = Genre.newGenre("Ação", false);

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "ac";
        final var expectedSort = "name";
        final var expectedDirection = "asc";

        final var expectedItemsCount = 1;
        final var expectedTotal = 1;

        final var expectedItems = List.of(genre);

        when(useCase.listGenres(any()))
                .thenReturn(new Pagination<>(expectedPage, expectedPerPage, expectedTotal, expectedItems));

        // when
        final var request = get("/genres")
                .queryParam("page", String.valueOf(expectedPage))
                .queryParam("perPage", String.valueOf(expectedPerPage))
                .queryParam("sort", expectedSort)
                .queryParam("dir", expectedDirection)
                .queryParam("search", expectedTerms)
                .accept(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(request);

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(expectedPage)))
                .andExpect(jsonPath("$.per_page", equalTo(expectedPerPage)))
                .andExpect(jsonPath("$.total", equalTo(expectedTotal)))
                .andExpect(jsonPath("$.items", hasSize(expectedItemsCount)))
                .andExpect(jsonPath("$.items[0].id", equalTo(genre.getId().getValue())))
                .andExpect(jsonPath("$.items[0].name", equalTo(genre.getName())))
                .andExpect(jsonPath("$.items[0].is_active", equalTo(genre.isActive())))
                .andExpect(jsonPath("$.items[0].created_at", equalTo(genre.getCreatedAt().toString())))
                .andExpect(jsonPath("$.items[0].deleted_at", equalTo(genre.getDeletedAt().toString())));

        verify(useCase).listGenres(argThat(query ->
                Objects.equals(expectedPage, query.page())
                        && Objects.equals(expectedPerPage, query.perPage())
                        && Objects.equals(expectedDirection, query.direction())
                        && Objects.equals(expectedSort, query.sort())
                        && Objects.equals(expectedTerms, query.terms())
        ));
    }

}
