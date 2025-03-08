package com.codelabs.admin.catalago.application.genre;

import com.codelabs.admin.catalago.IntegrationTest;
import com.codelabs.admin.catalago.application.ports.out.CategoryPort;
import com.codelabs.admin.catalago.application.ports.out.GenrePort;
import com.codelabs.admin.catalago.application.service.genre.GenreService;
import com.codelabs.admin.catalago.common.exceptions.NotFoundException;
import com.codelabs.admin.catalago.domain.category.Category;
import com.codelabs.admin.catalago.domain.category.CategoryID;
import com.codelabs.admin.catalago.domain.genre.Genre;
import com.codelabs.admin.catalago.domain.genre.GenreID;
import com.codelabs.admin.catalago.domain.pagination.SearchQuery;
import com.codelabs.admin.catalago.infrastructure.persistence.genre.entity.GenreEntity;
import com.codelabs.admin.catalago.infrastructure.persistence.genre.repositoy.GenreRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

@IntegrationTest
public class GenreServiceIT {

    @Autowired
    private GenreService service;

    @SpyBean
    private GenrePort genrePort;

    @SpyBean
    private CategoryPort categoryPort;

    @Autowired
    private GenreRepository genreRepository;

    @Test
    public void givenAValidGenre_whenCallsCreateGenre_shouldReturnGenreId() {
        // given
        final var filmes =
                categoryPort.save(Category.newCategory("Filmes", null, true));

        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(filmes.getId());

        final var genre =
                Genre.newGenre(expectedName, expectedIsActive)
                        .addCategories(expectedCategories);

        // when
        final var actualResponse = service.create(genre);

        // then
        Assertions.assertNotNull(actualResponse);
        Assertions.assertNotNull(actualResponse.getId());

        final var actualGenre = genreRepository.findById(actualResponse.getId().getValue()).get();

        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertTrue(
                expectedCategories.size() == actualGenre.getCategoryIDs().size()
                        && expectedCategories.containsAll(actualGenre.getCategoryIDs())
        );
        Assertions.assertNotNull(actualGenre.getCreatedAt());
        Assertions.assertNotNull(actualGenre.getUpdatedAt());
        Assertions.assertNull(actualGenre.getDeletedAt());
    }

    @Test
    public void givenAValidGenreWithoutCategories_whenCallsCreateGenre_shouldReturnGenreId() {
        // given
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();

        final var genre =
                Genre.newGenre(expectedName, expectedIsActive)
                        .addCategories(expectedCategories);

        // when
        final var actualResponse = service.create(genre);

        // then
        Assertions.assertNotNull(actualResponse);
        Assertions.assertNotNull(actualResponse.getId());

        final var actualGenre = genreRepository.findById(actualResponse.getId().getValue()).get();

        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertTrue(
                expectedCategories.size() == actualGenre.getCategoryIDs().size()
                        && expectedCategories.containsAll(actualGenre.getCategoryIDs())
        );
        Assertions.assertNotNull(actualGenre.getCreatedAt());
        Assertions.assertNotNull(actualGenre.getUpdatedAt());
        Assertions.assertNull(actualGenre.getDeletedAt());
    }

    @Test
    public void givenAValidGenre_whenCallsCreateGenreAndSomeCategoriesDoesNotExists_shouldReturnDomainException() {
        // given
        final var series =
                categoryPort.save(Category.newCategory("Séries", null, true));

        final var filmes = CategoryID.from("456");
        final var documentarios = CategoryID.from("789");

        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(filmes, series.getId(), documentarios);

        final var expectedErrorMessage = "Some categories could not be found: 456, 789";

        final var genre =
                Genre.newGenre(expectedName, expectedIsActive)
                        .addCategories(expectedCategories);

        // when
        final var actualException = Assertions.assertThrows(NotFoundException.class, () -> {
            service.create(genre);
        });

        // then
        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());

        Mockito.verify(categoryPort, times(1)).existsByIds(any());
        Mockito.verify(genrePort, times(0)).save(any());
    }

    @Test
    public void givenAValidGenreId_whenCallsDeleteGenre_shouldDeleteGenre() {
        // given
        final var genre = genrePort.save(Genre.newGenre("Ação", true));

        final var expectedId = genre.getId();

        Assertions.assertEquals(1, genreRepository.count());

        // when
        Assertions.assertDoesNotThrow(() -> service.deleteById(expectedId.getValue()));

        // when
        Assertions.assertEquals(0, genreRepository.count());
    }

    @Test
    public void givenAnInvalidGenreId_whenCallsDeleteGenre_shouldBeOk() {
        // given
        genrePort.save(Genre.newGenre("Ação", true));

        final var expectedId = GenreID.from("123");

        Assertions.assertEquals(1, genreRepository.count());

        // when
        Assertions.assertDoesNotThrow(() -> service.deleteById(expectedId.getValue()));

        // when
        Assertions.assertEquals(1, genreRepository.count());
    }

    @Test
    public void givenAValidGenre_whenCallsUpdateGenre_shouldReturnGenreUpdated() {
        // given
        final var genre = genrePort.save(Genre.newGenre("acao", true));

        final var expectedId = genre.getId();
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();

        final var genreUpdate = Genre.newGenre(expectedId.getValue(), expectedName,
                expectedIsActive).addCategories(expectedCategories);

        // when
        final var actualResponse = service.update(genreUpdate);

        // then
        Assertions.assertNotNull(actualResponse);
        Assertions.assertEquals(expectedId, actualResponse.getId());

        final var actualGenre = genreRepository.findById(genre.getId().getValue()).get();

        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertTrue(
                expectedCategories.size() == actualGenre.getCategoryIDs().size()
                        && expectedCategories.containsAll(actualGenre.getCategoryIDs())
        );
        Assertions.assertEquals(genre.getCreatedAt(), actualGenre.getCreatedAt());
        Assertions.assertTrue(genre.getUpdatedAt().isBefore(actualGenre.getUpdatedAt()));
        Assertions.assertNull(actualGenre.getDeletedAt());
    }

    @Test
    public void givenAValidGenreWithCategories_whenCallsUpdateGenre_shouldReturnGenreUpdated() {
        // given
        final var filmes =
                categoryPort.save(Category.newCategory("Filmes", null, true));

        final var series =
                categoryPort.save(Category.newCategory("Séries", null, true));

        final var genre = genrePort.save(Genre.newGenre("acao", true));

        final var expectedId = genre.getId();
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(filmes.getId(), series.getId());

        final var genreUpdate = Genre.newGenre(expectedId.getValue(), expectedName,
                expectedIsActive).addCategories(expectedCategories);

        // when
        final var actualResponse = service.update(genreUpdate);

        // then
        Assertions.assertNotNull(actualResponse);
        Assertions.assertEquals(expectedId, actualResponse.getId());

        final var actualGenre = genreRepository.findById(genre.getId().getValue()).get();

        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertTrue(
                expectedCategories.size() == actualGenre.getCategoryIDs().size()
                        && expectedCategories.containsAll(actualGenre.getCategoryIDs())
        );
        Assertions.assertEquals(genre.getCreatedAt(), actualGenre.getCreatedAt());
        Assertions.assertTrue(genre.getUpdatedAt().isBefore(actualGenre.getUpdatedAt()));
        Assertions.assertNull(actualGenre.getDeletedAt());
    }

    @Test
    public void givenAnValidGenre_whenCallsUpdateGenreAndSomeCategoriesDoesNotExists_shouldReturnNotFoundException() {
        // given
        final var filmes =
                categoryPort.save(Category.newCategory("Filems", null, true));

        final var series = CategoryID.from("456");
        final var documentarios = CategoryID.from("789");

        final var genre = genrePort.save(Genre.newGenre("acao", true));

        final var expectedId = genre.getId();
        final String expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(filmes.getId(), series, documentarios);

        final var expectedErrorMessage = "Some categories could not be found: 456, 789";


        final var genreUpdate = Genre.newGenre(expectedId.getValue(), expectedName,
                expectedIsActive).addCategories(expectedCategories);

        // when
        final var actualException = Assertions.assertThrows(NotFoundException.class, () -> {
            service.update(genreUpdate);
        });

        // then
        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());

        Mockito.verify(genrePort, times(1)).getById(eq(expectedId));

        Mockito.verify(categoryPort, times(1)).existsByIds(eq(expectedCategories));

    }

    @Test
    public void givenAValidId_whenCallsGetGenre_shouldReturnGenre() {
        // given
        final var series =
                categoryPort.save(Category.newCategory("Séries", null, true));

        final var filmes =
                categoryPort.save(Category.newCategory("Filmes", null, true));

        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(series.getId(), filmes.getId());

        final var genre = genrePort.save(
                Genre.newGenre(expectedName, expectedIsActive)
                        .addCategories(expectedCategories)
        );

        final var expectedId = genre.getId();

        // when
        final var actualGenre = service.getById(expectedId.getValue());

        // then
        Assertions.assertEquals(expectedId, actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertTrue(
                expectedCategories.size() == actualGenre.getCategories().size()
                        && expectedCategories.containsAll(actualGenre.getCategories())
        );
        Assertions.assertEquals(genre.getCreatedAt(), actualGenre.getCreatedAt());
        Assertions.assertEquals(genre.getUpdatedAt(), actualGenre.getUpdatedAt());
        Assertions.assertEquals(genre.getDeletedAt(), actualGenre.getDeletedAt());
    }

    @Test
    public void givenAValidId_whenCallsGetGenreAndDoesNotExists_shouldReturnNotFound() {
        // given
        final var expectedErrorMessage = "Genre not found in database with id 123";

        final var expectedId = GenreID.from("123");

        // when
        final var actualException = Assertions.assertThrows(NotFoundException.class, () -> {
            service.getById(expectedId.getValue());
        });

        // then
        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());
    }

    @Test
    public void givenAValidQuery_whenCallsListGenres_shouldReturnGenres() {
        // given
        final var genres = List.of(
                Genre.newGenre("Ação", true),
                Genre.newGenre("Aventura", true)
        );

        genreRepository.saveAllAndFlush(
                genres.stream()
                        .map(GenreEntity::from)
                        .toList()
        );

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "A";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedTotal = 2;

        final var query =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        // when
        final var actualResponse = service.listGenres(query);

        // then
        Assertions.assertEquals(expectedPage, actualResponse.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResponse.perPage());
        Assertions.assertEquals(expectedTotal, actualResponse.total());
        Assertions.assertTrue(
                genres.size() == actualResponse.items().size()
                        && genres.containsAll(actualResponse.items())
        );
    }

    @Test
    public void givenAValidQuery_whenCallsListGenresAndResultIsEmpty_shouldReturnGenres() {
        // given
        final var genres = List.<Genre>of();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "A";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedTotal = 0;

        final var query =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        // when
        final var actualResponse = service.listGenres(query);

        // then
        Assertions.assertEquals(expectedPage, actualResponse.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResponse.perPage());
        Assertions.assertEquals(expectedTotal, actualResponse.total());
        Assertions.assertEquals(genres, actualResponse.items());
    }


}
