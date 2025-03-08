package com.codelabs.admin.catalago.application.service.castmember;

import com.codelabs.admin.catalago.application.ports.in.CastMemberUseCase;
import com.codelabs.admin.catalago.application.ports.out.CastMemberPort;
import com.codelabs.admin.catalago.common.stereotype.UseCase;
import com.codelabs.admin.catalago.domain.castmember.CastMember;
import com.codelabs.admin.catalago.domain.castmember.CastMemberID;
import com.codelabs.admin.catalago.domain.pagination.Pagination;
import com.codelabs.admin.catalago.domain.pagination.SearchQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static net.logstash.logback.marker.Markers.append;

@UseCase
@RequiredArgsConstructor
@Slf4j
public class CastMemberService implements CastMemberUseCase {

    public static final String CAST_MEMBER_FIELD = "cast member";
    private final CastMemberPort castMemberPort;

    @Override
    public CastMember create(final CastMember castMember) {
        log.info(append(CAST_MEMBER_FIELD, castMember), "Starting cast member creation service");

        final var savedCastMember = this.castMemberPort.save(castMember);

        log.info("Cast member creation service completed successfully.");
        return savedCastMember;
    }

    @Override
    public void deleteById(final String id) {
        log.info(append("id", id), "delete cast member by id");

        this.castMemberPort.deleteById(CastMemberID.from(id));

        log.info("Cast member service completed successfully.");
    }

    @Override
    public CastMember getById(final String id) {
        log.info(append("id", id), "Search cast member by id");

        final var castMember = this.castMemberPort.getById(CastMemberID.from(id));
        log.info(append(CAST_MEMBER_FIELD, castMember), "Cast member successfully found.");

        return castMember;
    }

    @Override
    public CastMember update(final CastMember castMember) {
        log.info(append(CAST_MEMBER_FIELD, castMember), "Starting cat member update service");

        final var castMemberFound = this.castMemberPort.getById(castMember.getId());
        castMemberFound.update(castMember.getName(), castMember.getType());
        log.info(append(CAST_MEMBER_FIELD, castMemberFound), "Cast member update successfully.");

        return castMemberPort.save(castMemberFound);
    }

    @Override
    public Pagination<CastMember> listCastMembers(final SearchQuery query) {
        log.info(append("query", query), "Search all cast member by query params");

        final var castMembers = this.castMemberPort.listCastMembers(query);
        log.info(append("cast members", castMembers), "Found cast members");

        return castMembers;
    }
}
