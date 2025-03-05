package com.codelabs.admin.catalago.application.category;

import com.codelabs.admin.catalago.IntegrationTest;
import com.codelabs.admin.catalago.application.service.CategoryService;
import com.codelabs.admin.catalago.domain.category.Category;
import com.codelabs.admin.catalago.domain.category.CategoryID;
import com.codelabs.admin.catalago.domain.pagination.SearchQuery;
import com.codelabs.admin.catalago.infrastructure.persistence.category.entity.CategoryEntity;
import com.codelabs.admin.catalago.infrastructure.persistence.category.repository.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.stream.Stream;

@IntegrationTest
public class CategoryServiceIT {

    @Autowired
    private CategoryService service;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void givenAValidCategory_whenCallsCreateCategory_shouldReturnCategoryId() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        Assertions.assertEquals(0, categoryRepository.count());

        final var category =
                Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        final var actualResponse = service.create(category);

        Assertions.assertNotNull(actualResponse);
        Assertions.assertNotNull(actualResponse.getId());

        Assertions.assertEquals(1, categoryRepository.count());

        final var actualCategory =
                categoryRepository.findById(actualResponse.getId().getValue()).get();

        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertNotNull(actualCategory.getCreatedAt());
        Assertions.assertNotNull(actualCategory.getUpdatedAt());
        Assertions.assertNull(actualCategory.getDeletedAt());
    }

    @Test
    public void givenAValidCCategoryWithInactiveCategory_whenCallsCreateCategory_shouldReturnInactiveCategoryId() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        Assertions.assertEquals(0, categoryRepository.count());

        final var category =
                Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        final var actualResponse = service.create(category);

        Assertions.assertNotNull(actualResponse);
        Assertions.assertNotNull(actualResponse.getId());

        Assertions.assertEquals(1, categoryRepository.count());

        final var actualCategory =
                categoryRepository.findById(actualResponse.getId().getValue()).get();

        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertNotNull(actualCategory.getCreatedAt());
        Assertions.assertNotNull(actualCategory.getUpdatedAt());
        Assertions.assertNotNull(actualCategory.getDeletedAt());
    }

    @Test
    public void givenAValidId_whenCallsDeleteCategory_shouldBeOK() {
        final var category = Category.newCategory("Filmes", "A categoria mais assistida", true);
        final var expectedId = category.getId();

        save(category);

        Assertions.assertEquals(1, categoryRepository.count());

        Assertions.assertDoesNotThrow(() -> service.deleteById(expectedId.getValue()));

        Assertions.assertEquals(0, categoryRepository.count());
    }

    @Test
    public void givenAInvalidId_whenCallsDeleteCategory_shouldBeOK() {
        final var expectedId = CategoryID.from("123");

        Assertions.assertEquals(0, categoryRepository.count());

        Assertions.assertDoesNotThrow(() -> service.deleteById(expectedId.getValue()));

        Assertions.assertEquals(0, categoryRepository.count());
    }

    @Test
    public void givenAValidCategory_whenCallsUpdateCategory_shouldReturnCategoryId() {
        final var category =
                Category.newCategory("Film", null, true);

        save(category);

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedId = category.getId();

        final var updateCategory = Category.newCategory(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedIsActive
        );

        Assertions.assertEquals(1, categoryRepository.count());

        final var actualResponse = service.update(updateCategory);

        Assertions.assertNotNull(actualResponse);
        Assertions.assertNotNull(actualResponse.getId());

        final var actualCategory =
                categoryRepository.findById(expectedId.getValue()).get();

        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertEquals(category.getCreatedAt().truncatedTo(ChronoUnit.SECONDS), actualCategory.getCreatedAt().truncatedTo(ChronoUnit.SECONDS));
        Assertions.assertTrue(category.getUpdatedAt().isBefore(actualCategory.getUpdatedAt()));
        Assertions.assertNull(actualCategory.getDeletedAt());
    }

    @Test
    public void givenAValidInactivateCategory_whenCallsUpdateCategory_shouldReturnInactiveCategoryId() {
        final var category =
                Category.newCategory("Film", null, true);

        save(category);

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;
        final var expectedId = category.getId();

        final var updateCategory = Category.newCategory(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedIsActive
        );

        Assertions.assertTrue(category.isActive());
        Assertions.assertNull(category.getDeletedAt());

        final var actualResponse = service.update(updateCategory);

        Assertions.assertNotNull(actualResponse);
        Assertions.assertNotNull(actualResponse.getId());

        final var actualCategory =
                categoryRepository.findById(expectedId.getValue()).get();

        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertEquals(category.getCreatedAt().truncatedTo(ChronoUnit.SECONDS), actualCategory.getCreatedAt().truncatedTo(ChronoUnit.SECONDS));
        Assertions.assertTrue(category.getUpdatedAt().isBefore(actualCategory.getUpdatedAt()));
        Assertions.assertNotNull(actualCategory.getDeletedAt());
    }

    @Test
    public void givenAValidId_whenCallsGetCategory_shouldReturnCategory() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var category =
                Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        final var expectedId = category.getId();

        save(category);

        final var actualCategory = service.getById(expectedId.getValue());

        Assertions.assertEquals(expectedId, actualCategory.getId());
        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertEquals(category.getCreatedAt().truncatedTo(ChronoUnit.SECONDS), actualCategory.getCreatedAt().truncatedTo(ChronoUnit.SECONDS));
        Assertions.assertEquals(category.getUpdatedAt().truncatedTo(ChronoUnit.SECONDS), actualCategory.getUpdatedAt().truncatedTo(ChronoUnit.SECONDS));
        Assertions.assertEquals(category.getDeletedAt(), actualCategory.getDeletedAt());
    }

    @Test
    public void givenAValidTerm_whenTermDoesntMatchsPrePersisted_shouldReturnEmptyPage() {
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "ji1j3i 1j3i1oj";
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedItemsCount = 0;
        final var expectedTotal = 0;

        mockListCategories();

        final var query =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var actualResponse = service.listCategories(query);

        Assertions.assertEquals(expectedItemsCount, actualResponse.items().size());
        Assertions.assertEquals(expectedPage, actualResponse.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResponse.perPage());
        Assertions.assertEquals(expectedTotal, actualResponse.total());
    }

    @ParameterizedTest
    @CsvSource({
            "fil,0,10,1,1,Filmes",
            "net,0,10,1,1,Netflix Originals",
            "ZON,0,10,1,1,Amazon Originals",
            "KI,0,10,1,1,Kids",
            "crianças,0,10,1,1,Kids",
            "da Amazon,0,10,1,1,Amazon Originals",
    })
    public void givenAValidTerm_whenCallsListCategories_shouldReturnCategoriesFiltered(
            final String expectedTerms,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedCategoryName
    ) {
        final var expectedSort = "name";
        final var expectedDirection = "asc";

        mockListCategories();

        final var query =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var actualResult = service.listCategories(query);

        Assertions.assertEquals(expectedItemsCount, actualResult.items().size());
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedCategoryName, actualResult.items().get(0).getName());
    }

    @ParameterizedTest
    @CsvSource({
            "name,asc,0,10,7,7,Amazon Originals",
            "name,desc,0,10,7,7,Sports",
    })
    public void givenAValidSortAndDirection_whenCallsListCategories_thenShouldReturnCategoriesOrdered(
            final String expectedSort,
            final String expectedDirection,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedCategoryName
    ) {
        final var expectedTerms = "";

        mockListCategories();

        final var query =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var actualResult = service.listCategories(query);

        System.out.println(actualResult.items());

        Assertions.assertEquals(expectedItemsCount, actualResult.items().size());
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedCategoryName, actualResult.items().get(0).getName());
    }

    @ParameterizedTest
    @CsvSource({
            "0,2,2,7,Amazon Originals;Documentários",
            "1,2,2,7,Filmes;Kids",
            "2,2,2,7,Netflix Originals;Series",
            "3,2,1,7,Sports",
    })
    public void givenAValidPage_whenCallsListCategories_shouldReturnCategoriesPaginated(
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedCategoriesName
    ) {
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedTerms = "";

        mockListCategories();

        final var query =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var actualResult = service.listCategories(query);

        Assertions.assertEquals(expectedItemsCount, actualResult.items().size());
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());

        int index = 0;
        for (final String expectedName : expectedCategoriesName.split(";")) {
            final String actualName = actualResult.items().get(index).getName();
            Assertions.assertEquals(expectedName, actualName);
            index++;
        }
    }

    private void mockListCategories() {
        final var categories = Stream.of(
                        Category.newCategory("Filmes", null, true),
                        Category.newCategory("Netflix Originals", "Títulos de autoria da Netflix", true),
                        Category.newCategory("Amazon Originals", "Títulos de autoria da Amazon Prime", true),
                        Category.newCategory("Documentários", null, true),
                        Category.newCategory("Sports", null, true),
                        Category.newCategory("Kids", "Categoria para crianças", true),
                        Category.newCategory("Series", null, true)
                )
                .map(CategoryEntity::from)
                .toList();

        categoryRepository.saveAllAndFlush(categories);
    }

    private void save(final Category... aCategory) {
        categoryRepository.saveAllAndFlush(
                Arrays.stream(aCategory)
                        .map(CategoryEntity::from)
                        .toList()
        );
    }

}
