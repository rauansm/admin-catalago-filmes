package com.codelabs.admin.catalago.infrastructure.persistence.castmember;

import com.codelabs.admin.catalago.MySQLAdapterTest;
import com.codelabs.admin.catalago.domain.castmember.CastMember;
import com.codelabs.admin.catalago.domain.castmember.CastMemberID;
import com.codelabs.admin.catalago.domain.enums.CastMemberType;
import com.codelabs.admin.catalago.domain.pagination.SearchQuery;
import com.codelabs.admin.catalago.infrastructure.persistence.castmember.adapter.CastMemberPersistenceAdapter;
import com.codelabs.admin.catalago.infrastructure.persistence.castmember.entity.CastMemberEntity;
import com.codelabs.admin.catalago.infrastructure.persistence.castmember.repository.CastMemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.codelabs.admin.catalago.Fixture.CastMember.type;
import static com.codelabs.admin.catalago.Fixture.name;

@MySQLAdapterTest
public class CastMemberPersistenceAdapterTest {

    @Autowired
    private CastMemberPersistenceAdapter memberAdapter;

    @Autowired
    private CastMemberRepository castMemberRepository;

    @Test
    public void givenAValidCastMember_whenCallsSave_shouldPersistIt() {
        // given
        final var expectedName = name();
        final var expectedType = type();

        final var member = CastMember.newMember(expectedName, expectedType);
        final var expectedId = member.getId();

        Assertions.assertEquals(0, castMemberRepository.count());

        // when
        final var actualMember = memberAdapter.save(CastMember.with(member));

        // then
        Assertions.assertEquals(1, castMemberRepository.count());

        Assertions.assertEquals(expectedId, actualMember.getId());
        Assertions.assertEquals(expectedName, actualMember.getName());
        Assertions.assertEquals(expectedType, actualMember.getType());
        Assertions.assertEquals(member.getCreatedAt(), actualMember.getCreatedAt());
        Assertions.assertEquals(member.getUpdatedAt(), actualMember.getUpdatedAt());

        final var persistedMember = castMemberRepository.findById(expectedId.getValue()).get();

        Assertions.assertEquals(expectedId.getValue(), persistedMember.getId());
        Assertions.assertEquals(expectedName, persistedMember.getName());
        Assertions.assertEquals(expectedType, persistedMember.getType());
        Assertions.assertEquals(member.getCreatedAt(), persistedMember.getCreatedAt());
        Assertions.assertEquals(member.getUpdatedAt(), persistedMember.getUpdatedAt());
    }

    @Test
    public void givenAValidCastMember_whenCallsDeleteById_shouldDeleteIt() {
        // given
        final var member = CastMember.newMember(name(), type());

        castMemberRepository.saveAndFlush(CastMemberEntity.from(member));

        Assertions.assertEquals(1, castMemberRepository.count());

        // when
        memberAdapter.deleteById(member.getId());

        // then
        Assertions.assertEquals(0, castMemberRepository.count());
    }

    @Test
    public void givenAnInvalidId_whenCallsDeleteById_shouldBeIgnored() {
        // given
        final var member = CastMember.newMember(name(), type());

        castMemberRepository.saveAndFlush(CastMemberEntity.from(member));

        Assertions.assertEquals(1, castMemberRepository.count());

        // when
        memberAdapter.deleteById(CastMemberID.from("123"));

        // then
        Assertions.assertEquals(1, castMemberRepository.count());
    }

    @Test
    public void givenAValidCastMember_whenCallsGetById_shouldReturnIt() {
        // given
        final var expectedName = name();
        final var expectedType = type();

        final var member = CastMember.newMember(expectedName, expectedType);
        final var expectedId = member.getId();

        castMemberRepository.saveAndFlush(CastMemberEntity.from(member));

        Assertions.assertEquals(1, castMemberRepository.count());

        // when
        final var actualMember = memberAdapter.getById(expectedId);

        // then
        Assertions.assertEquals(expectedId, actualMember.getId());
        Assertions.assertEquals(expectedName, actualMember.getName());
        Assertions.assertEquals(expectedType, actualMember.getType());
        Assertions.assertEquals(member.getCreatedAt(), actualMember.getCreatedAt());
        Assertions.assertEquals(member.getUpdatedAt(), actualMember.getUpdatedAt());
    }

    @Test
    public void givenEmptyCastMembers_whenCallsListCastMembers_shouldReturnEmpty() {
        // given
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedTotal = 0;

        final var query =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        // when
        final var actualPage = memberAdapter.listCastMembers(query);

        // then
        Assertions.assertEquals(expectedPage, actualPage.currentPage());
        Assertions.assertEquals(expectedPerPage, actualPage.perPage());
        Assertions.assertEquals(expectedTotal, actualPage.total());
        Assertions.assertEquals(expectedTotal, actualPage.items().size());
    }

    @ParameterizedTest
    @CsvSource({
            "vin,0,10,1,1,Vin Diesel",
            "taran,0,10,1,1,Quentin Tarantino",
            "jas,0,10,1,1,Jason Momoa",
            "har,0,10,1,1,Kit Harington",
            "MAR,0,10,1,1,Martin Scorsese",
    })
    public void givenAValidTerm_whenCallsListCastMembers_shouldReturnFiltered(
            final String expectedTerms,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedName
    ) {
        // given
        mockMembers();

        final var expectedSort = "name";
        final var expectedDirection = "asc";

        final var query =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        // when
        final var actualPage = memberAdapter.listCastMembers(query);

        // then
        Assertions.assertEquals(expectedPage, actualPage.currentPage());
        Assertions.assertEquals(expectedPerPage, actualPage.perPage());
        Assertions.assertEquals(expectedTotal, actualPage.total());
        Assertions.assertEquals(expectedItemsCount, actualPage.items().size());
        Assertions.assertEquals(expectedName, actualPage.items().get(0).getName());
    }

    @ParameterizedTest
    @CsvSource({
            "name,asc,0,10,5,5,Jason Momoa",
            "name,desc,0,10,5,5,Vin Diesel",
            "createdAt,asc,0,10,5,5,Kit Harington",
    })
    public void givenAValidSortAndDirection_whenCallsListCastMembers_shouldReturnSorted(
            final String expectedSort,
            final String expectedDirection,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedName
    ) {
        // given
        mockMembers();

        final var expectedTerms = "";

        final var query =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        // when
        final var actualPage = memberAdapter.listCastMembers(query);

        // then
        Assertions.assertEquals(expectedPage, actualPage.currentPage());
        Assertions.assertEquals(expectedPerPage, actualPage.perPage());
        Assertions.assertEquals(expectedTotal, actualPage.total());
        Assertions.assertEquals(expectedItemsCount, actualPage.items().size());
        Assertions.assertEquals(expectedName, actualPage.items().get(0).getName());
    }

    @ParameterizedTest
    @CsvSource({
            "0,2,2,5,Jason Momoa;Kit Harington",
            "1,2,2,5,Martin Scorsese;Quentin Tarantino",
            "2,2,1,5,Vin Diesel",
    })
    public void givenAValidPagination_whenCallsListCastMembers_shouldReturnPaginated(
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedNames
    ) {
        // given
        mockMembers();

        final var expectedTerms = "";
        final var expectedSort = "name";
        final var expectedDirection = "asc";

        final var query =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        // when
        final var actualPage = memberAdapter.listCastMembers(query);

        // then
        Assertions.assertEquals(expectedPage, actualPage.currentPage());
        Assertions.assertEquals(expectedPerPage, actualPage.perPage());
        Assertions.assertEquals(expectedTotal, actualPage.total());
        Assertions.assertEquals(expectedItemsCount, actualPage.items().size());

        int index = 0;
        for (final var expectedName : expectedNames.split(";")) {
            Assertions.assertEquals(expectedName, actualPage.items().get(index).getName());
            index++;
        }
    }

    private void mockMembers() {
        castMemberRepository.saveAllAndFlush(List.of(
                CastMemberEntity.from(CastMember.newMember("Kit Harington", CastMemberType.ACTOR)),
                CastMemberEntity.from(CastMember.newMember("Vin Diesel", CastMemberType.ACTOR)),
                CastMemberEntity.from(CastMember.newMember("Quentin Tarantino", CastMemberType.DIRECTOR)),
                CastMemberEntity.from(CastMember.newMember("Jason Momoa", CastMemberType.ACTOR)),
                CastMemberEntity.from(CastMember.newMember("Martin Scorsese", CastMemberType.DIRECTOR))
        ));
    }
}
