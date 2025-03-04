package com.codelabs.admin.catalago.application.service.genre;

import com.codelabs.admin.catalago.application.ports.out.CategoryPort;
import com.codelabs.admin.catalago.application.ports.out.GenrePort;
import com.codelabs.admin.catalago.application.service.GenreService;
import com.codelabs.admin.catalago.common.exceptions.NotFoundException;
import com.codelabs.admin.catalago.domain.category.CategoryID;
import com.codelabs.admin.catalago.domain.genre.Genre;
import com.codelabs.admin.catalago.domain.genre.GenreID;
import com.codelabs.admin.catalago.domain.pagination.Pagination;
import com.codelabs.admin.catalago.domain.pagination.SearchQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Objects;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

public class GenreServiceTest {

    private GenreService service;
    private CategoryPort categoryPort;
    private GenrePort genrePort;

    @BeforeEach
    void setup() {
        this.categoryPort = mock(CategoryPort.class);
        this.genrePort = mock(GenrePort.class);
        this.service = new GenreService(this.genrePort, this.categoryPort);
    }

    @Test
    public void givenAValidGenre_whenCallsCreateGenre_shouldReturnGenreId() {
        // given
        final var expectName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();

        final var newGenre =
                Genre.newGenre(expectName, expectedIsActive);
        newGenre.addCategories(expectedCategories);

        when(genrePort.save(any()))
                .thenReturn(newGenre);

        // when
        final var actualResponse = service.create(newGenre);

        // then
        Assertions.assertNotNull(actualResponse);
        Assertions.assertNotNull(actualResponse.getId());

        Mockito.verify(genrePort, times(1)).save(argThat(genre ->
                Objects.equals(expectName, genre.getName())
                        && Objects.equals(expectedIsActive, genre.isActive())
                        && Objects.equals(expectedCategories, genre.getCategories())
                        && Objects.nonNull(genre.getId())
                        && Objects.nonNull(genre.getCreatedAt())
                        && Objects.nonNull(genre.getUpdatedAt())
                        && Objects.isNull(genre.getDeletedAt())
        ));
    }

    @Test
    public void givenAValidGenreWithInactiveGenre_whenCallsCreateGenre_shouldReturnGenreId() {
        // given
        final var expectName = "Ação";
        final var expectedIsActive = false;
        final var expectedCategories = List.<CategoryID>of();

        final var newGenre =
                Genre.newGenre(expectName, expectedIsActive);
        newGenre.addCategories(expectedCategories);

        when(genrePort.save(any()))
                .thenReturn(newGenre);

        // when
        final var actualResponse = service.create(newGenre);

        // then
        Assertions.assertNotNull(actualResponse);
        Assertions.assertNotNull(actualResponse.getId());

        Mockito.verify(genrePort, times(1)).save(argThat(genre ->
                Objects.equals(expectName, genre.getName())
                        && Objects.equals(expectedIsActive, genre.isActive())
                        && Objects.equals(expectedCategories, genre.getCategories())
                        && Objects.nonNull(genre.getId())
                        && Objects.nonNull(genre.getCreatedAt())
                        && Objects.nonNull(genre.getUpdatedAt())
                        && Objects.nonNull(genre.getDeletedAt())
        ));
    }

    @Test
    public void givenAValidGenre_whenCallsCreateGenreAndSomeCategoriesDoesNotExists_shouldReturnNotFoundException() {
        // given
        final var filmes = CategoryID.from("456");
        final var series = CategoryID.from("123");
        final var documentarios = CategoryID.from("789");

        final var expectName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(filmes, series, documentarios);

        final var expectedErrorMessage = "Some categories could not be found: 456, 789";

        when(categoryPort.existsByIds(any()))
                .thenReturn(List.of(series));

        final var newGenre =
                Genre.newGenre(expectName, expectedIsActive);
        newGenre.addCategories(expectedCategories);

        // when
        final var actualException = Assertions.assertThrows(NotFoundException.class, () -> {
            service.create(newGenre);
        });

        // then
        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());

        Mockito.verify(categoryPort, times(1)).existsByIds(any());
        Mockito.verify(genrePort, times(0)).save(any());
    }

    @Test
    public void givenAValidGenre_whenCallsUpdateGenre_shouldReturnGenreId() {
        // given
        final var genre = Genre.newGenre("acao", true);

        final var expectedId = genre.getId();
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();

        final var updateGenre = Genre.newGenre(expectedId.getValue(), expectedName, expectedIsActive);
        updateGenre.addCategories(expectedCategories);

        when(genrePort.getById(any()))
                .thenReturn((genre.clone()));

        when(genrePort.save(any()))
                .thenAnswer(returnsFirstArg());
        // when
        final var actualResponse = service.update(updateGenre);

        // then
        Assertions.assertNotNull(actualResponse);
        Assertions.assertEquals(expectedId.getValue(), actualResponse.getId().getValue());

        Mockito.verify(genrePort, times(1)).getById(eq(expectedId));

        Mockito.verify(genrePort, times(1)).save(argThat(updatedGenre ->
                Objects.equals(expectedId, updatedGenre.getId())
                        && Objects.equals(expectedName, updatedGenre.getName())
                        && Objects.equals(expectedIsActive, updatedGenre.isActive())
                        && Objects.equals(expectedCategories, updatedGenre.getCategories())
                        && Objects.equals(genre.getCreatedAt(), updatedGenre.getCreatedAt())
                        && genre.getUpdatedAt().isBefore(updatedGenre.getUpdatedAt())
                        && Objects.isNull(updatedGenre.getDeletedAt())
        ));
    }

    @Test
    public void givenAValidGenre_whenCallsUpdateGenreAndSomeCategoriesDoesNotExists_shouldReturnNotFoundException() {
        // given
        final var filmes = CategoryID.from("123");
        final var series = CategoryID.from("456");
        final var documentarios = CategoryID.from("789");

        final var genre = Genre.newGenre("acao", true);

        final var expectedId = genre.getId();
        final String expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(filmes, series, documentarios);

        final var expectedErrorMessage = "Some categories could not be found: 456, 789";

        final var updateGenre = Genre.newGenre(expectedId.getValue(), expectedName, expectedIsActive);
        updateGenre.addCategories(expectedCategories);

        when(genrePort.getById(any()))
                .thenReturn(genre.clone());

        when(categoryPort.existsByIds(any()))
                .thenReturn(List.of(filmes));

        // when
        final var actualException = Assertions.assertThrows(NotFoundException.class, () -> {
            service.update(updateGenre);
        });

        // then
        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());

        Mockito.verify(genrePort, times(1)).getById(eq(expectedId));

        Mockito.verify(categoryPort, times(1)).existsByIds(eq(expectedCategories));

        Mockito.verify(genrePort, times(0)).save(any());
    }

    @Test
    public void givenAValidGenreId_whenCallsDeleteGenre_shouldDeleteGenre() {
        // given
        final var genre = Genre.newGenre("Ação", true);

        final var expectedId = genre.getId();

        doNothing()
                .when(genrePort).deleteById(any());

        // when
        Assertions.assertDoesNotThrow(() -> service.deleteById(expectedId.getValue()));

        // when
        Mockito.verify(genrePort, times(1)).deleteById(expectedId);
    }

    @Test
    public void givenAnInvalidGenreId_whenCallsDeleteGenre_shouldBeOk() {
        // given
        final var expectedId = GenreID.from("123");

        doNothing()
                .when(genrePort).deleteById(any());
        // when
        Assertions.assertDoesNotThrow(() -> service.deleteById(expectedId.getValue()));

        // when
        Mockito.verify(genrePort, times(1)).deleteById(expectedId);
    }

    @Test
    public void givenAValidId_whenCallsGetGenre_shouldReturnGenre() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(
                CategoryID.from("123"),
                CategoryID.from("456")
        );

        final var genre =
                Genre.newGenre(expectedName, expectedIsActive);
        genre.addCategories(expectedCategories);

        final var expectedId = genre.getId();

        when(genrePort.getById(eq(expectedId)))
                .thenReturn(genre.clone());

        final var actualGenre = service.getById(expectedId.getValue());

        Assertions.assertEquals(expectedId.getValue(), actualGenre.getId().getValue());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(expectedCategories, actualGenre.getCategories());
        Assertions.assertEquals(genre.getCreatedAt(), actualGenre.getCreatedAt());
        Assertions.assertEquals(genre.getUpdatedAt(), actualGenre.getUpdatedAt());
        Assertions.assertEquals(genre.getDeletedAt(), actualGenre.getDeletedAt());

        Mockito.verify(genrePort, times(1)).getById(eq(expectedId));
    }

    @Test
    public void givenAValidQuery_whenCallsListGenres_shouldReturnGenres() {
        // given
        final var genres = List.of(
                Genre.newGenre("Ação", true),
                Genre.newGenre("Aventura", true)
        );

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "A";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedTotal = 2;

        final var expectedPagination = new Pagination<>(
                expectedPage,
                expectedPerPage,
                expectedTotal,
                genres
        );

        when(genrePort.listGenres(any()))
                .thenReturn(expectedPagination);

        final var query =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        // when
        final var actualResponse = service.listGenres(query);

        // then
        Assertions.assertEquals(expectedPage, actualResponse.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResponse.perPage());
        Assertions.assertEquals(expectedTotal, actualResponse.total());
        Assertions.assertEquals(genres, actualResponse.items());

        Mockito.verify(genrePort, times(1)).listGenres(eq(query));
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

        final var expectedPagination = new Pagination<>(
                expectedPage,
                expectedPerPage,
                expectedTotal,
                genres
        );

        when(genrePort.listGenres(any()))
                .thenReturn(expectedPagination);

        final var query =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        // when
        final var actualOutput = service.listGenres(query);

        // then
        Assertions.assertEquals(expectedPage, actualOutput.currentPage());
        Assertions.assertEquals(expectedPerPage, actualOutput.perPage());
        Assertions.assertEquals(expectedTotal, actualOutput.total());
        Assertions.assertEquals(genres, actualOutput.items());

        Mockito.verify(genrePort, times(1)).listGenres(eq(query));
    }

}