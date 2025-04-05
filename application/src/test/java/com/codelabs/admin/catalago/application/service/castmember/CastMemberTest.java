package com.codelabs.admin.catalago.application.service.castmember;

import com.codelabs.admin.catalago.application.ports.out.CastMemberPort;
import com.codelabs.admin.catalago.application.service.Fixture;
import com.codelabs.admin.catalago.domain.castmember.CastMember;
import com.codelabs.admin.catalago.domain.castmember.CastMemberID;
import com.codelabs.admin.catalago.domain.enums.CastMemberType;
import com.codelabs.admin.catalago.domain.pagination.Pagination;
import com.codelabs.admin.catalago.domain.pagination.SearchQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

public class CastMemberTest {

    private CastMemberService service;
    private CastMemberPort castMemberPort;

    @BeforeEach
    void setup() {
        this.castMemberPort = mock(CastMemberPort.class);
        this.service = new CastMemberService(this.castMemberPort);
    }

    @Test
    public void givenAValidCastMember_whenCallsCreateCastMember_shouldReturnIt() {
        // given
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMembers.type();

        final var castMember = CastMember.newMember(expectedName, expectedType);

        when(castMemberPort.save(any()))
                .thenAnswer(returnsFirstArg());

        // when
        final var actualResponse = service.create(castMember);

        // then
        Assertions.assertNotNull(actualResponse);
        Assertions.assertNotNull(actualResponse.getId());

        verify(castMemberPort).save(argThat(member ->
                Objects.nonNull(member.getId())
                        && Objects.equals(expectedName, member.getName())
                        && Objects.equals(expectedType, member.getType())
                        && Objects.nonNull(member.getCreatedAt())
                        && Objects.nonNull(member.getUpdatedAt())
        ));
    }

    @Test
    public void givenAValidId_whenCallsDeleteCastMember_shouldDeleteIt() {
        // given
        final var aMember = CastMember.newMember(Fixture.name(), Fixture.CastMembers.type());

        final var expectedId = aMember.getId();

        doNothing()
                .when(castMemberPort).deleteById(any());

        // when
        Assertions.assertDoesNotThrow(() -> service.deleteById(expectedId.getValue()));

        // then
        verify(castMemberPort).deleteById(eq(expectedId));
    }

    @Test
    public void givenAnInvalidId_whenCallsDeleteCastMember_shouldBeOk() {
        // given
        final var expectedId = CastMemberID.from("123");

        doNothing()
                .when(castMemberPort).deleteById(any());

        // when
        Assertions.assertDoesNotThrow(() -> service.deleteById(expectedId.getValue()));

        // then
        verify(castMemberPort).deleteById(eq(expectedId));
    }

    @Test
    public void givenAValidId_whenCallsGetCastMember_shouldReturnIt() {
        // given
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMembers.type();

        final var member = CastMember.newMember(expectedName, expectedType);

        final var expectedId = member.getId();

        when(castMemberPort.getById(any()))
                .thenReturn(member);

        // when
        final var actualResponse = service.getById(expectedId.getValue());

        // then
        Assertions.assertNotNull(actualResponse);
        Assertions.assertEquals(expectedId, actualResponse.getId());
        Assertions.assertEquals(expectedName, actualResponse.getName());
        Assertions.assertEquals(expectedType, actualResponse.getType());
        Assertions.assertEquals(member.getCreatedAt(), actualResponse.getCreatedAt());
        Assertions.assertEquals(member.getUpdatedAt(), actualResponse.getUpdatedAt());

        verify(castMemberPort).getById(eq(expectedId));
    }

    @Test
    public void givenAValidCastMember_whenCallsUpdateCastMember_shouldReturnItsIdentifier() {
        // given
        final var member = CastMember.newMember("vin diesel", CastMemberType.DIRECTOR);

        final var expectedId = member.getId();
        final var expectedName = Fixture.name();
        final var expectedType = CastMemberType.ACTOR;

        final var castMember = CastMember.newMember(
                expectedId.getValue(),
                expectedName,
                expectedType
        );

        when(castMemberPort.getById(any()))
                .thenReturn(CastMember.with(member));

        when(castMemberPort.save(any()))
                .thenAnswer(returnsFirstArg());

        // when
        final var actualResponse = service.update(castMember);

        // then
        Assertions.assertNotNull(actualResponse);
        Assertions.assertEquals(expectedId, actualResponse.getId());

        verify(castMemberPort).getById(eq(expectedId));

        verify(castMemberPort).save(argThat(aUpdatedMember ->
                Objects.equals(expectedId, aUpdatedMember.getId())
                        && Objects.equals(expectedName, aUpdatedMember.getName())
                        && Objects.equals(expectedType, aUpdatedMember.getType())
                        && Objects.equals(member.getCreatedAt(), aUpdatedMember.getCreatedAt())
                        && member.getUpdatedAt().isBefore(aUpdatedMember.getUpdatedAt())
        ));
    }

    @Test
    public void givenAValidQuery_whenCallsListCastMembers_shouldReturnAll() {
        // given
        final var members = List.of(
                CastMember.newMember(Fixture.name(), Fixture.CastMembers.type()),
                CastMember.newMember(Fixture.name(), Fixture.CastMembers.type())
        );

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "Algo";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedTotal = 2;

        final var expectedPagination = new Pagination<>(
                expectedPage,
                expectedPerPage,
                expectedTotal,
                members
        );

        when(castMemberPort.listCastMembers(any()))
                .thenReturn(expectedPagination);

        final var query =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        // when
        final var actualResponse = service.listCastMembers(query);

        // then
        Assertions.assertEquals(expectedPage, actualResponse.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResponse.perPage());
        Assertions.assertEquals(expectedTotal, actualResponse.total());
        Assertions.assertEquals(members, actualResponse.items());

        verify(castMemberPort).listCastMembers(eq(query));
    }

    @Test
    public void givenAValidQuery_whenCallsListCastMembersAndIsEmpty_shouldReturn() {
        // given
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "Algo";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedTotal = 0;

        final var members = List.<CastMember>of();

        final var expectedPagination = new Pagination<>(
                expectedPage,
                expectedPerPage,
                expectedTotal,
                members
        );

        when(castMemberPort.listCastMembers(any()))
                .thenReturn(expectedPagination);

        final var query =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        // when
        final var actualResponse = service.listCastMembers(query);

        // then
        Assertions.assertEquals(expectedPage, actualResponse.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResponse.perPage());
        Assertions.assertEquals(expectedTotal, actualResponse.total());
        Assertions.assertEquals(members, actualResponse.items());

        verify(castMemberPort).listCastMembers(eq(query));
    }


}
