package com.codelabs.admin.catalago.infrastructure.web.in.castmember;

import br.com.fluentvalidator.context.Error;
import br.com.fluentvalidator.context.ValidationResult;
import com.codelabs.admin.catalago.ControllerTest;
import com.codelabs.admin.catalago.Fixture;
import com.codelabs.admin.catalago.application.ports.in.CastMemberUseCase;
import com.codelabs.admin.catalago.common.exceptions.NotFoundException;
import com.codelabs.admin.catalago.domain.castmember.CastMember;
import com.codelabs.admin.catalago.domain.castmember.CastMemberID;
import com.codelabs.admin.catalago.domain.enums.ProblemType;
import com.codelabs.admin.catalago.domain.pagination.Pagination;
import com.codelabs.admin.catalago.infrastructure.web.in.castmember.dto.CastMemberDetailsResponse;
import com.codelabs.admin.catalago.infrastructure.web.in.castmember.dto.CastMemberRequest;
import com.codelabs.admin.catalago.infrastructure.web.in.castmember.dto.CastMemberResponse;
import com.codelabs.admin.catalago.infrastructure.web.in.castmember.mapper.CastMemberControllerMapper;
import com.codelabs.admin.catalago.infrastructure.web.in.castmember.validator.CastMemberValidator;
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

@ControllerTest(controllers = CastMemberAPI.class)
public class CastMemberAPITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CastMemberUseCase castMemberUseCase;

    @MockBean
    private CastMemberValidator validator;

    @MockBean
    private CastMemberControllerMapper mapper;

    @Test
    public void givenAValidDto_whenCallsCreateCastMember_shouldReturnItsIdentifier() throws Exception {
        // given
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMember.type();
        final var expectedId = CastMemberID.from("o1i2u3i1o");

        final var memberRequest = new CastMemberRequest(expectedName, expectedType);
        final var memberDomain = CastMember.newMember(expectedId.getValue(), expectedName, expectedType);

        when(validator.validate(any(CastMemberRequest.class))).thenReturn(ValidationResult.ok());
        when(mapper.toDomain(any())).thenReturn(memberDomain);
        when(castMemberUseCase.create(any())).thenReturn(memberDomain);
        when(mapper.toResponse(any(), any())).thenReturn(CastMemberResponse.from(expectedId));

        // when
        final var request = post("/cast_members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberRequest));

        final var response = this.mvc.perform(request)
                .andDo(print());

        // then
        response.andExpect(status().isCreated())
                .andExpect(header().string("Location", "/cast_members/" + expectedId.getValue()))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(expectedId.getValue())));

        verify(castMemberUseCase).create(argThat(actualMember ->
                Objects.equals(expectedName, actualMember.getName())
                        && Objects.equals(expectedType, actualMember.getType())
        ));
    }

    @Test
    public void givenAnInvalidName_whenCallsCreateCastMember_shouldReturnPhysicalValidationException() throws Exception {
        // given
        final String expectedName = null;
        final var expectedType = Fixture.CastMember.type();

        final var expectedField = "name";
        final var expectedCode = ProblemType.NAME_MUST_NOT_BE_NULL.name();
        final var expectedMessage = ProblemType.NAME_MUST_NOT_BE_NULL.getDescription();

        final var memberRequest = new CastMemberRequest(expectedName, expectedType);

        when(validator.validate(any(CastMemberRequest.class)))
                .thenReturn(ValidationResult.fail(List.of(Error.create(expectedField, expectedMessage, expectedCode, null))));

        // when
        final var request = post("/cast_members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberRequest));

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

        verify(validator, times(1)).validate(any(CastMemberRequest.class));
    }

    @Test
    public void givenAValidId_whenCallsGetById_shouldReturnIt() throws Exception {
        // given
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMember.type();

        final var member = CastMember.newMember(expectedName, expectedType);
        final var expectedId = member.getId().getValue();

        when(castMemberUseCase.getById(any()))
                .thenReturn(member);

        when(mapper.toResponse(any(), any()))
                .thenReturn(CastMemberDetailsResponse.from(member));

        // when
        final var request = get("/cast_members/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(request);

        // then
        response.andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(expectedId)))
                .andExpect(jsonPath("$.name", equalTo(expectedName)))
                .andExpect(jsonPath("$.type", equalTo(expectedType.name())))
                .andExpect(jsonPath("$.created_at", equalTo(member.getCreatedAt().toString())))
                .andExpect(jsonPath("$.updated_at", equalTo(member.getUpdatedAt().toString())));

        verify(castMemberUseCase).getById(eq(expectedId));
    }

    @Test
    public void givenAInvalidId_whenCallsGetByIdAndCastMemberDoesntExists_shouldReturnNotFound() throws Exception {
        // given
        final var expectedId = "5f82365c-ab4c-42a3-aed4-2d103588e7b7";
        final var expectedErrorMessage = "Cast member not found in database with id " + expectedId;

        when(castMemberUseCase.getById(any()))
                .thenThrow(new NotFoundException("Cast member not found in database with id " + expectedId));

        // when
        final var request = get("/cast_members/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(request);

        // then
        response.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));

        verify(castMemberUseCase).getById(eq(expectedId));
    }

    @Test
    public void givenAValidDto_whenCallsUpdateCastMember_shouldReturnItsIdentifier() throws Exception {
        // given
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMember.type();

        final var member = CastMember.newMember(expectedName, expectedType);
        final var expectedId = member.getId();

        final var memberRequest = new CastMemberRequest(expectedName, expectedType);
        final var memberDomain = CastMember.newMember(expectedId.getValue(), expectedName, expectedType);

        when(validator.validate(any(CastMemberRequest.class))).thenReturn(ValidationResult.ok());
        when(mapper.toDomain(any(), any())).thenReturn(memberDomain);
        when(castMemberUseCase.update(any())).thenReturn(memberDomain);
        when(mapper.toResponse(any(), any())).thenReturn(CastMemberResponse.from(expectedId));

        // when
        final var request = put("/cast_members/{id}", expectedId.getValue())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberRequest));

        final var response = this.mvc.perform(request)
                .andDo(print());

        // then
        response.andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(expectedId.getValue())));

        verify(castMemberUseCase).update(argThat(actualMember ->
                Objects.equals(expectedId, actualMember.getId())
                        && Objects.equals(expectedName, actualMember.getName())
                        && Objects.equals(expectedType, actualMember.getType())
        ));
    }

    @Test
    public void givenAValidId_whenCallsDeleteById_shouldDeleteIt() throws Exception {
        // given
        final var expectedId = "5f82365c-ab4c-42a3-aed4-2d103588e7b7";

        doNothing()
                .when(castMemberUseCase).deleteById(any());

        // when
        final var request = delete("/cast_members/{id}", expectedId);

        final var response = this.mvc.perform(request);

        // then
        response.andExpect(status().isNoContent());

        verify(castMemberUseCase).deleteById(eq(expectedId));
    }

    @Test
    public void givenValidParams_whenCallListCastMembers_shouldReturnIt() throws Exception {
        // given
        final var member = CastMember.newMember(Fixture.name(), Fixture.CastMember.type());

        final var expectedPage = 1;
        final var expectedPerPage = 20;
        final var expectedTerms = "Alg";
        final var expectedSort = "type";
        final var expectedDirection = "desc";

        final var expectedItemsCount = 1;
        final var expectedTotal = 1;

        final var castMemberList = List.of(member);

        when(castMemberUseCase.listCastMembers(any()))
                .thenReturn(new Pagination<>(expectedPage, expectedPerPage, expectedTotal, castMemberList));

        // when
        final var request = get("/cast_members")
                .queryParam("page", String.valueOf(expectedPage))
                .queryParam("perPage", String.valueOf(expectedPerPage))
                .queryParam("search", expectedTerms)
                .queryParam("sort", expectedSort)
                .queryParam("dir", expectedDirection)
                .accept(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(request);

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(expectedPage)))
                .andExpect(jsonPath("$.per_page", equalTo(expectedPerPage)))
                .andExpect(jsonPath("$.total", equalTo(expectedTotal)))
                .andExpect(jsonPath("$.items", hasSize(expectedItemsCount)))
                .andExpect(jsonPath("$.items[0].id", equalTo(member.getId().getValue())))
                .andExpect(jsonPath("$.items[0].name", equalTo(member.getName())))
                .andExpect(jsonPath("$.items[0].type", equalTo(member.getType().name())))
                .andExpect(jsonPath("$.items[0].created_at", equalTo(member.getCreatedAt().toString())));

        verify(castMemberUseCase).listCastMembers(argThat(query ->
                Objects.equals(expectedPage, query.page())
                        && Objects.equals(expectedPerPage, query.perPage())
                        && Objects.equals(expectedTerms, query.terms())
                        && Objects.equals(expectedSort, query.sort())
                        && Objects.equals(expectedDirection, query.direction())
        ));
    }

}
