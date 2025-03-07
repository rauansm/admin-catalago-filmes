package com.codelabs.admin.catalago.infrastructure.web.in.castmember;

import com.codelabs.admin.catalago.application.ports.in.CastMemberUseCase;
import com.codelabs.admin.catalago.common.exceptions.PhysicalValidationException;
import com.codelabs.admin.catalago.domain.castmember.CastMember;
import com.codelabs.admin.catalago.domain.pagination.Pagination;
import com.codelabs.admin.catalago.domain.pagination.SearchQuery;
import com.codelabs.admin.catalago.infrastructure.web.in.castmember.dto.CastMemberDetailsResponse;
import com.codelabs.admin.catalago.infrastructure.web.in.castmember.dto.CastMemberListResponse;
import com.codelabs.admin.catalago.infrastructure.web.in.castmember.dto.CastMemberRequest;
import com.codelabs.admin.catalago.infrastructure.web.in.castmember.dto.CastMemberResponse;
import com.codelabs.admin.catalago.infrastructure.web.in.castmember.mapper.CastMemberControllerMapper;
import com.codelabs.admin.catalago.infrastructure.web.in.castmember.validator.CastMemberValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static net.logstash.logback.marker.Markers.append;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CastMemberController implements CastMemberAPI {

    private static final String CAST_MEMBER_FIELD = "cast member";
    public static final String CAST_MEMBER_WAS_MAPPED_SUCCESSFULLY = "Cast member was mapped successfully";
    private final CastMemberUseCase castMemberUseCase;
    private final CastMemberControllerMapper mapper;
    private final CastMemberValidator validator;

    @Override
    public ResponseEntity<CastMemberResponse> create(final CastMemberRequest request) {
        log.info(append("body", request), "Receiving cast member creation request");

        validator.validate(request).isInvalidThrow(PhysicalValidationException.class);

        final CastMember castMember = mapper.toDomain(request);
        log.info(append(CAST_MEMBER_FIELD, castMember), CAST_MEMBER_WAS_MAPPED_SUCCESSFULLY);

        final CastMember castMemberCreated = castMemberUseCase.create(castMember);
        log.info(append(CAST_MEMBER_FIELD, castMemberCreated), "Cast member was created successfully");

        final CastMemberResponse memberResponse = mapper.toResponse(castMemberCreated, CastMemberResponse::from);
        log.info(append(CAST_MEMBER_FIELD, memberResponse), "Created cast member was mapped successfully");

        return ResponseEntity.created(URI.create("/cast_members/" + memberResponse.id()))
                .body(memberResponse);
    }

    @Override
    public Pagination<CastMemberListResponse> listCastMembers(final String search, final int page, final int perPage,
                                                              final String sort, final String direction) {
        log.info(append("search", search)
                        .and(append("page", page)
                                .and(append("perPage", perPage)
                                        .and(append("sort", sort)
                                                .and(append("dir", direction))))),
                "Receiving request to list cast members by parameters");

        final Pagination<CastMember> castMemberPagination = castMemberUseCase.listCastMembers(new SearchQuery(page, perPage, search, sort, direction));
        log.info(append(CAST_MEMBER_FIELD, castMemberPagination), "cast members found");

        final Pagination<CastMemberListResponse> listResponse = castMemberPagination.map(CastMemberListResponse::from);
        log.info(append(CAST_MEMBER_FIELD, listResponse), "Cast members successfully mapped");
        return listResponse;
    }

    @Override
    public CastMemberDetailsResponse getById(final String id) {
        log.info("Receiving cast members search request {}", id);

        final CastMember castMember = castMemberUseCase.getById(id);
        log.info(append(CAST_MEMBER_FIELD, castMember), "Cast member found");

        final CastMemberDetailsResponse detailsResponse = mapper.toResponse(castMember, CastMemberDetailsResponse::from);
        log.info(append(CAST_MEMBER_FIELD, detailsResponse), CAST_MEMBER_WAS_MAPPED_SUCCESSFULLY);
        return detailsResponse;
    }

    @Override
    public ResponseEntity<CastMemberResponse> updateById(final String id, final CastMemberRequest request) {
        log.info(append("id", id).and(append("body", request)), "Receiving cast member update request");

        validator.validate(request).isInvalidThrow(PhysicalValidationException.class);

        final CastMember castMember = mapper.toDomain(id, request);
        log.info(append(CAST_MEMBER_FIELD, castMember), CAST_MEMBER_WAS_MAPPED_SUCCESSFULLY);

        final CastMember updatedCastMember = castMemberUseCase.update(castMember);
        log.info(append(CAST_MEMBER_FIELD, castMember), "Cast member was update successfully");

        final CastMemberResponse castMemberResponse = mapper.toResponse(updatedCastMember, CastMemberResponse::from);
        log.info(append(CAST_MEMBER_FIELD, castMemberResponse), "Updated cast member was mapped successfully");

        return ResponseEntity.ok(castMemberResponse);
    }

    @Override
    public void deleteById(final String id) {
        log.info("Receiving cast member deletion request {}", id);

        castMemberUseCase.deleteById(id);

        log.info("deleted cast member successfully");
    }
}
