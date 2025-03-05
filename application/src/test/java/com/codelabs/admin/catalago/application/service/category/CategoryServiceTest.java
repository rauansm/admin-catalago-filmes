package com.codelabs.admin.catalago.application.service.category;

import com.codelabs.admin.catalago.application.ports.out.CategoryPort;
import com.codelabs.admin.catalago.application.service.CategoryService;
import com.codelabs.admin.catalago.domain.category.Category;
import com.codelabs.admin.catalago.domain.category.CategoryID;
import com.codelabs.admin.catalago.domain.pagination.SearchQuery;
import com.codelabs.admin.catalago.domain.pagination.Pagination;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Objects;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class CategoryServiceTest {

    private CategoryService service;
    private CategoryPort categoryPort;

    @BeforeEach
    void setup() {
        this.categoryPort = mock(CategoryPort.class);
        this.service = new CategoryService(this.categoryPort);
    }

    @Test
    public void givenAValidCategory_whenCallsCreateCategory_shouldReturnCategoryId() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var category =
                Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        when(categoryPort.save(category))
                .thenAnswer(returnsFirstArg());

        final var response = service.create(category);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getId());

        Mockito.verify(categoryPort, times(1)).save(argThat(savedCategory ->
                Objects.equals(expectedName, savedCategory.getName())
                        && Objects.equals(expectedDescription, savedCategory.getDescription())
                        && Objects.equals(expectedIsActive, savedCategory.isActive())
                        && Objects.nonNull(savedCategory.getId())
                        && Objects.nonNull(savedCategory.getCreatedAt())
                        && Objects.nonNull(savedCategory.getUpdatedAt())
                        && Objects.isNull(savedCategory.getDeletedAt())
        ));
    }

    @Test
    public void givenAValidCategory_whenCallsUpdateCategory_shouldReturnCategoryId() {
        final var actualCategory =
                Category.newCategory("Film", null, true);

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedId = actualCategory.getId();

        final var updateCategory = Category.newCategory(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedIsActive);

        when(categoryPort.getById(eq(expectedId)))
                .thenReturn((actualCategory.clone()));

        when(categoryPort.save(any()))
                .thenAnswer(returnsFirstArg());

        final var categoryResponse = service.update(updateCategory);
        Assertions.assertNotNull(categoryResponse);
        Assertions.assertNotNull(categoryResponse.getId());

        Mockito.verify(categoryPort, times(1)).getById(eq(expectedId));

        Mockito.verify(categoryPort, times(1)).save(argThat(
                updatedCategory ->
                        Objects.equals(expectedName, updatedCategory.getName())
                                && Objects.equals(expectedDescription, updatedCategory.getDescription())
                                && Objects.equals(expectedIsActive, updatedCategory.isActive())
                                && Objects.equals(expectedId, updatedCategory.getId())
                                && Objects.equals(actualCategory.getCreatedAt(), updatedCategory.getCreatedAt())
                                && actualCategory.getUpdatedAt().isBefore(updatedCategory.getUpdatedAt())
                                && Objects.isNull(updatedCategory.getDeletedAt())
        ));
    }

    @Test
    public void givenAValidInactivateCategory_whenCallsUpdateCategory_shouldReturnInactiveCategoryId() {
        final var actualCategory =
                Category.newCategory("Film", null, true);

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;
        final var expectedId = actualCategory.getId();

        final var updateCategory = Category.newCategory(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedIsActive);

        when(categoryPort.getById(eq(expectedId)))
                .thenReturn((actualCategory.clone()));

        when(categoryPort.save(any(Category.class)))
                .thenReturn(updateCategory);

        final var categoryResponse = service.update(updateCategory);

        Assertions.assertNotNull(categoryResponse);
        Assertions.assertNotNull(categoryResponse.getId());

        Mockito.verify(categoryPort, times(1)).getById(eq(expectedId));

        Mockito.verify(categoryPort, times(1)).save(argThat(
                updatedCategory ->
                        Objects.equals(expectedName, updatedCategory.getName())
                                && Objects.equals(expectedDescription, updatedCategory.getDescription())
                                && Objects.equals(expectedIsActive, updatedCategory.isActive())
                                && Objects.equals(expectedId, updatedCategory.getId())
                                && Objects.equals(actualCategory.getCreatedAt(), updatedCategory.getCreatedAt())
                                && actualCategory.getUpdatedAt().isBefore(updatedCategory.getUpdatedAt())
                                && Objects.nonNull(updatedCategory.getDeletedAt())
        ));
    }

    @Test
    public void givenAValidId_whenCallsDeleteCategory_shouldBeOK() {
        final var category = Category.newCategory("Filmes", "A categoria mais assistida", true);
        final var expectedId = category.getId();

        doNothing()
                .when(categoryPort).deleteById(eq(expectedId));

        Assertions.assertDoesNotThrow(() -> service.deleteById(expectedId.getValue()));

        Mockito.verify(categoryPort, times(1)).deleteById(eq(expectedId));
    }

    @Test
    public void givenAInvalidId_whenCallsDeleteCategory_shouldBeOK() {
        final var expectedId = CategoryID.from("123");

        doNothing()
                .when(categoryPort).deleteById(eq(expectedId));

        Assertions.assertDoesNotThrow(() -> service.deleteById(expectedId.getValue()));

        Mockito.verify(categoryPort, times(1)).deleteById(eq(expectedId));
    }

    @Test
    public void givenAValidId_whenCallsGetCategory_shouldReturnCategory() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCategory =
                Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        final var expectedId = aCategory.getId();

        when(categoryPort.getById(eq(expectedId)))
                .thenReturn(aCategory.clone());

        final var actualCategory = service.getById(expectedId.getValue());

        Assertions.assertEquals(expectedId, actualCategory.getId());
        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertEquals(aCategory.getCreatedAt(), actualCategory.getCreatedAt());
        Assertions.assertEquals(aCategory.getUpdatedAt(), actualCategory.getUpdatedAt());
        Assertions.assertEquals(aCategory.getDeletedAt(), actualCategory.getDeletedAt());
    }

    @Test
    public void givenAValidQuery_whenCallsListCategories_thenShouldReturnCategories() {
        final var categories = List.of(
                Category.newCategory("Filmes", null, true),
                Category.newCategory("Series", null, true)
        );

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";

        final var query =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var expectedPagination =
                new Pagination<>(expectedPage, expectedPerPage, categories.size(), categories);

        final var expectedItemsCount = 2;

        when(categoryPort.listCategories(eq(query)))
                .thenReturn(expectedPagination);

        final var actualResult = service.listCategories(query);

        Assertions.assertEquals(expectedItemsCount, actualResult.items().size());
        Assertions.assertEquals(expectedPagination, actualResult);
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(categories.size(), actualResult.total());
    }

    @Test
    public void givenAValidQuery_whenHasNoResults_thenShouldReturnEmptyCategories() {
        final var categories = List.<Category>of();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";

        final var query =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var expectedPagination =
                new Pagination<>(expectedPage, expectedPerPage, categories.size(), categories);

        final var expectedItemsCount = 0;

        when(categoryPort.listCategories(eq(query)))
                .thenReturn(expectedPagination);

        final var actualResult = service.listCategories(query);

        Assertions.assertEquals(expectedItemsCount, actualResult.items().size());
        Assertions.assertEquals(expectedPagination, actualResult);
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(categories.size(), actualResult.total());
    }

}