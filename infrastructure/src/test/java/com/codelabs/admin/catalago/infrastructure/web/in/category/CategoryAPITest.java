package com.codelabs.admin.catalago.infrastructure.web.in.category;

import br.com.fluentvalidator.context.Error;
import br.com.fluentvalidator.context.ValidationResult;
import com.codelabs.admin.catalago.ControllerTest;
import com.codelabs.admin.catalago.application.ports.in.CategoryUseCase;
import com.codelabs.admin.catalago.common.exceptions.NotFoundException;
import com.codelabs.admin.catalago.domain.category.Category;
import com.codelabs.admin.catalago.domain.enums.ProblemType;
import com.codelabs.admin.catalago.domain.pagination.Pagination;
import com.codelabs.admin.catalago.infrastructure.web.in.category.dto.CategoryDetailsResponse;
import com.codelabs.admin.catalago.infrastructure.web.in.category.dto.CategoryRequest;
import com.codelabs.admin.catalago.infrastructure.web.in.category.dto.CategoryResponse;
import com.codelabs.admin.catalago.infrastructure.web.in.category.mapper.CategoryControllerMapper;
import com.codelabs.admin.catalago.infrastructure.web.in.category.validator.CategoryValidator;
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

@ControllerTest(controllers = CategoryAPI.class)
public class CategoryAPITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryUseCase useCase;

    @MockBean
    private CategoryControllerMapper mapper;

    @MockBean
    private CategoryValidator validator;

    @Test
    public void givenAValidRequest_whenCallsCreateCategory_shouldReturnCategoryId() throws Exception {
        // Given
        final var expectedId = "123";
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var input = new CategoryRequest(expectedName, expectedDescription, expectedIsActive);
        final var category = Category.newCategory(expectedId, expectedName, expectedDescription, expectedIsActive);

        when(validator.validate(any(CategoryRequest.class))).thenReturn(ValidationResult.ok());
        when(mapper.toDomain(any())).thenReturn(category);
        when(useCase.create(any())).thenReturn(category);
        when(mapper.toResponse(any(), any())).thenReturn(CategoryResponse.from(expectedId));

        // When
        final var request = post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(input));

        final var response = this.mvc.perform(request).andDo(print());

        // Then
        response.andExpect(status().isCreated())
                .andExpect(header().string("Location", "/categories/" + expectedId))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(expectedId)));

        verify(validator, times(1)).validate(any(CategoryRequest.class));

        verify(mapper, times(1)).toDomain(any());

        verify(mapper, times(1)).toResponse(any(), any());

        verify(useCase, times(1)).create(argThat(categoryDomain ->
                Objects.equals(expectedName, categoryDomain.getName()) &&
                        Objects.equals(expectedDescription, categoryDomain.getDescription()) &&
                        Objects.equals(expectedIsActive, categoryDomain.isActive())
        ));
    }

    @Test
    public void givenInvalidName_whenCallsValidator_thenShouldReturnPhysicalValidationException() throws Exception {
        // given
        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var expectedField = "name";
        final var expectedCode = ProblemType.NAME_MUST_NOT_BE_NULL.name();
        final var expectedMessage = ProblemType.NAME_MUST_NOT_BE_NULL.getDescription();

        final var input = new CategoryRequest(expectedName, expectedDescription, expectedIsActive);

        when(validator.validate(any(CategoryRequest.class)))
                .thenReturn(ValidationResult.fail(List.of(Error.create(expectedField, expectedMessage, expectedCode, null))));

        // when
        final var request = post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(input));

        final var response = this.mvc.perform(request).andDo(print());

        // then
        response.andExpect(status().isBadRequest())
                .andExpect(header().string("Location", nullValue()))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.fields", hasSize(1)))
                .andExpect(jsonPath("$.fields[0].field", equalTo(expectedField)))
                .andExpect(jsonPath("$.fields[0].code", equalTo(expectedCode)))
                .andExpect(jsonPath("$.fields[0].message", equalTo(expectedMessage)));

        verify(validator, times(1)).validate(any(CategoryRequest.class));
    }

    @Test
    public void givenAValidId_whenCallsGetCategory_shouldReturnCategory() throws Exception {
        // given
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var category =
                Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        final var expectedId = category.getId().getValue();

        when(useCase.getById(any()))
                .thenReturn(category);

        when(mapper.toResponse(any(), any()))
                .thenReturn(CategoryDetailsResponse.from(category));

        // when
        final var request = get("/categories/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(request)
                .andDo(print());

        // then
        response.andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(expectedId)))
                .andExpect(jsonPath("$.name", equalTo(expectedName)))
                .andExpect(jsonPath("$.description", equalTo(expectedDescription)))
                .andExpect(jsonPath("$.is_active", equalTo(expectedIsActive)))
                .andExpect(jsonPath("$.created_at", equalTo(category.getCreatedAt().toString())))
                .andExpect(jsonPath("$.updated_at", equalTo(category.getUpdatedAt().toString())))
                .andExpect(jsonPath("$.deleted_at", equalTo(category.getDeletedAt())));

        verify(useCase, times(1)).getById(eq(expectedId));
        verify(mapper, times(1)).toResponse(any(), any());
    }

    @Test
    public void givenAInvalidId_whenCallsGetCategory_shouldReturnNotFound() throws Exception {
        // given
        final var expectedId = "5f82365c-ab4c-42a3-aed4-2d103588e7b7";
        final var expectedErrorMessage = "Category not found in database with id " + expectedId;

        when(useCase.getById(any()))
                .thenThrow(new NotFoundException("Category not found in database with id " + expectedId));

        // when
        final var request = get("/categories/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(request)
                .andDo(print());

        // then
        response.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));
    }

    @Test
    public void givenAValidCategory_whenCallsUpdateCategory_shouldReturnCategoryId() throws Exception {
        // given
        final var expectedId = "5f82365c-ab4c-42a3-aed4-2d103588e7b7";
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var input = new CategoryRequest(expectedName, expectedDescription, expectedIsActive);
        final var category = Category.newCategory(expectedId, expectedName, expectedDescription, expectedIsActive);

        when(validator.validate(any(CategoryRequest.class))).thenReturn(ValidationResult.ok());
        when(mapper.toDomain(any(), any())).thenReturn(category);
        when(useCase.update(any())).thenReturn(category);
        when(mapper.toResponse(any(), any())).thenReturn(CategoryResponse.from(expectedId));

        // when
        final var request = put("/categories/{id}", expectedId)
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
                        && Objects.equals(expectedDescription, categoryUpdate.getDescription())
                        && Objects.equals(expectedIsActive, categoryUpdate.isActive())
        ));
    }

    @Test
    public void givenAInvalidName_whenCallsUpdateCategory_thenShouldReturnException() throws Exception {
        // given
        final var expectedId = "5f82365c-ab4c-42a3-aed4-2d103588e7b7";
        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var expectedField = "name";
        final var expectedCode = ProblemType.NAME_MUST_NOT_BE_NULL.name();
        final var expectedMessage = ProblemType.NAME_MUST_NOT_BE_NULL.getDescription();

        final var input = new CategoryRequest(expectedName, expectedDescription, expectedIsActive);

        when(validator.validate(any(CategoryRequest.class)))
                .thenReturn(ValidationResult.fail(List.of(Error.create(expectedField, expectedMessage, expectedCode, null))));

        // when
        final var request = put("/categories/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input));

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

        verify(validator, times(1)).validate(any(CategoryRequest.class));
    }

    @Test
    public void givenAValidId_whenCallsDeleteCategory_shouldReturnNoContent() throws Exception {
        // given
        final var expectedId = "5f82365c-ab4c-42a3-aed4-2d103588e7b7";

        doNothing()
                .when(useCase).deleteById(any());

        // when
        final var request = delete("/categories/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(request)
                .andDo(print());

        // then
        response.andExpect(status().isNoContent());

        verify(useCase, times(1)).deleteById(eq(expectedId));
    }

    @Test
    public void givenValidParams_whenCallsListCategories_shouldReturnCategories() throws Exception {
        // given
        final var category = Category.newCategory("Movies", null, true);

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "movies";
        final var expectedSort = "description";
        final var expectedDirection = "desc";
        final var expectedItemsCount = 1;
        final var expectedTotal = 1;

        final var categoryList = List.of(category);

        when(useCase.listCategories(any()))
                .thenReturn(new Pagination<>(expectedPage, expectedPerPage, expectedTotal, categoryList));

        // when
        final var request = get("/categories")
                .queryParam("page", String.valueOf(expectedPage))
                .queryParam("perPage", String.valueOf(expectedPerPage))
                .queryParam("sort", expectedSort)
                .queryParam("dir", expectedDirection)
                .queryParam("search", expectedTerms)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(request)
                .andDo(print());

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(expectedPage)))
                .andExpect(jsonPath("$.per_page", equalTo(expectedPerPage)))
                .andExpect(jsonPath("$.total", equalTo(expectedTotal)))
                .andExpect(jsonPath("$.items", hasSize(expectedItemsCount)))
                .andExpect(jsonPath("$.items[0].id", equalTo(category.getId().getValue())))
                .andExpect(jsonPath("$.items[0].name", equalTo(category.getName())))
                .andExpect(jsonPath("$.items[0].description", equalTo(category.getDescription())))
                .andExpect(jsonPath("$.items[0].is_active", equalTo(category.isActive())))
                .andExpect(jsonPath("$.items[0].created_at", equalTo(category.getCreatedAt().toString())))
                .andExpect(jsonPath("$.items[0].deleted_at", equalTo(category.getDeletedAt())));

        verify(useCase, times(1)).listCategories(argThat(query ->
                Objects.equals(expectedPage, query.page())
                        && Objects.equals(expectedPerPage, query.perPage())
                        && Objects.equals(expectedDirection, query.direction())
                        && Objects.equals(expectedSort, query.sort())
                        && Objects.equals(expectedTerms, query.terms())
        ));
    }

}
