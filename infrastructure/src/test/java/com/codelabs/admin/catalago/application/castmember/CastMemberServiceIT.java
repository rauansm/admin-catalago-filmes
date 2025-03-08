package com.codelabs.admin.catalago.application.castmember;

import com.codelabs.admin.catalago.Fixture;
import com.codelabs.admin.catalago.IntegrationTest;
import com.codelabs.admin.catalago.application.ports.out.CastMemberPort;
import com.codelabs.admin.catalago.application.service.castmember.CastMemberService;
import com.codelabs.admin.catalago.common.exceptions.NotFoundException;
import com.codelabs.admin.catalago.domain.castmember.CastMember;
import com.codelabs.admin.catalago.domain.castmember.CastMemberID;
import com.codelabs.admin.catalago.domain.enums.CastMemberType;
import com.codelabs.admin.catalago.domain.pagination.SearchQuery;
import com.codelabs.admin.catalago.infrastructure.persistence.castmember.entity.CastMemberEntity;
import com.codelabs.admin.catalago.infrastructure.persistence.castmember.repository.CastMemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@IntegrationTest
public class CastMemberServiceIT {

    @Autowired
    private CastMemberService service;

    @Autowired
    private CastMemberRepository castMemberRepository;

    @SpyBean
    private CastMemberPort castMemberPort;

    @Test
    public void givenAValidCastMember_whenCallsCreateCastMember_shouldReturnCastMemberCreated() {
        // given
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMember.type();

        final var member = CastMember.newMember(expectedName, expectedType);

        // when
        final var actualResponse = service.create(member);

        // then
        Assertions.assertNotNull(actualResponse);
        Assertions.assertNotNull(actualResponse.getId());

        final var actualMember = this.castMemberRepository.findById(actualResponse.getId().getValue()).get();

        Assertions.assertEquals(expectedName, actualMember.getName());
        Assertions.assertEquals(expectedType, actualMember.getType());
        Assertions.assertNotNull(actualMember.getCreatedAt());
        Assertions.assertNotNull(actualMember.getUpdatedAt());
        Assertions.assertEquals(actualMember.getCreatedAt(), actualMember.getUpdatedAt());

        verify(castMemberPort).save(any());
    }

    @Test
    public void givenAValidId_whenCallsDeleteCastMember_shouldDeleteIt() {
        // given
        final var member = CastMember.newMember(Fixture.name(), Fixture.CastMember.type());
        final var memberTwo = CastMember.newMember(Fixture.name(), Fixture.CastMember.type());

        final var expectedId = member.getId();

        this.castMemberRepository.saveAndFlush(CastMemberEntity.from(member));
        this.castMemberRepository.saveAndFlush(CastMemberEntity.from(memberTwo));

        Assertions.assertEquals(2, this.castMemberRepository.count());

        // when
        Assertions.assertDoesNotThrow(() -> service.deleteById(expectedId.getValue()));

        // then
        verify(castMemberPort).deleteById(eq(expectedId));

        Assertions.assertEquals(1, this.castMemberRepository.count());
        Assertions.assertFalse(this.castMemberRepository.existsById(expectedId.getValue()));
        Assertions.assertTrue(this.castMemberRepository.existsById(memberTwo.getId().getValue()));
    }

    @Test
    public void givenAnInvalidId_whenCallsDeleteCastMember_shouldBeOk() {
        // given
        this.castMemberRepository.saveAndFlush(
                CastMemberEntity.from(
                        CastMember.newMember(Fixture.name(), Fixture.CastMember.type())
                )
        );

        final var expectedId = CastMemberID.from("123");

        Assertions.assertEquals(1, this.castMemberRepository.count());

        // when
        Assertions.assertDoesNotThrow(() -> service.deleteById(expectedId.getValue()));

        // then
        verify(castMemberPort).deleteById(eq(expectedId));

        Assertions.assertEquals(1, this.castMemberRepository.count());
    }

    @Test
    public void givenAValidCastMember_whenCallsUpdateCastMember_shouldReturnCastMemberUpdated() {
        // given
        final var member = CastMember.newMember("vin diesel", CastMemberType.DIRECTOR);

        this.castMemberRepository.saveAndFlush(CastMemberEntity.from(member));

        final var expectedId = member.getId();
        final var expectedName = Fixture.name();
        final var expectedType = CastMemberType.ACTOR;

        final var updateMember = CastMember.newMember(
                expectedId.getValue(),
                expectedName,
                expectedType
        );

        // when
        final var actualResponse = service.update(updateMember);

        // then
        Assertions.assertNotNull(actualResponse);
        Assertions.assertEquals(expectedId, actualResponse.getId());

        final var actualPersistedMember =
                this.castMemberRepository.findById(expectedId.getValue()).get();

        Assertions.assertEquals(expectedName, actualPersistedMember.getName());
        Assertions.assertEquals(expectedType, actualPersistedMember.getType());
        Assertions.assertEquals(member.getCreatedAt(), actualPersistedMember.getCreatedAt());
        Assertions.assertTrue(member.getUpdatedAt().isBefore(actualPersistedMember.getUpdatedAt()));

        verify(castMemberPort).getById(any());
        verify(castMemberPort).save(any());
    }

    @Test
    public void givenAValidId_whenCallsGetCastMember_shouldReturnIt() {
        // given
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMember.type();

        final var member = CastMember.newMember(expectedName, expectedType);

        final var expectedId = member.getId();

        this.castMemberRepository.saveAndFlush(CastMemberEntity.from(member));

        Assertions.assertEquals(1, this.castMemberRepository.count());

        // when
        final var actualResponse = service.getById(expectedId.getValue());

        // then
        Assertions.assertNotNull(actualResponse);
        Assertions.assertEquals(expectedId, actualResponse.getId());
        Assertions.assertEquals(expectedName, actualResponse.getName());
        Assertions.assertEquals(expectedType, actualResponse.getType());
        Assertions.assertEquals(member.getCreatedAt(), actualResponse.getCreatedAt());
        Assertions.assertEquals(member.getUpdatedAt(), actualResponse.getUpdatedAt());

        verify(castMemberPort).getById(any());
    }


    @Test
    public void givenAInvalidId_whenCallsGetCastMemberAndDoesNotExists_shouldReturnNotFoundException() {
        // given
        final var expectedId = CastMemberID.from("123");

        final var expectedErrorMessage = "Cast member not found in database with id 123";

        // when
        final var actualOutput = Assertions.assertThrows(NotFoundException.class, () -> {
            service.getById(expectedId.getValue());
        });

        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertEquals(expectedErrorMessage, actualOutput.getMessage());

        verify(castMemberPort).getById(eq(expectedId));
    }

    @Test
    public void givenAValidQuery_whenCallsListCastMembers_shouldReturnAll() {
        // given
        final var members = List.of(
                CastMember.newMember(Fixture.name(), Fixture.CastMember.type()),
                CastMember.newMember(Fixture.name(), Fixture.CastMember.type())
        );

        this.castMemberRepository.saveAllAndFlush(
                members.stream()
                        .map(CastMemberEntity::from)
                        .toList()
        );

        Assertions.assertEquals(2, this.castMemberRepository.count());

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedTotal = 2;

        final var query =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        // when
        final var actualResponse = service.listCastMembers(query);

        // then
        Assertions.assertEquals(expectedPage, actualResponse.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResponse.perPage());
        Assertions.assertEquals(expectedTotal, actualResponse.total());
        Assertions.assertTrue(
                members.size() == actualResponse.items().size()
                        && members.containsAll(actualResponse.items())
        );

        verify(castMemberPort).listCastMembers(any());
    }

}
