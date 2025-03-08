package com.codelabs.admin.catalago.application.ports.out;

import com.codelabs.admin.catalago.domain.castmember.CastMember;
import com.codelabs.admin.catalago.domain.castmember.CastMemberID;
import com.codelabs.admin.catalago.domain.pagination.Pagination;
import com.codelabs.admin.catalago.domain.pagination.SearchQuery;

import java.util.List;

public interface CastMemberPort {
    CastMember save(final CastMember castMember);

    CastMember getById(final CastMemberID id);

    void deleteById(final CastMemberID id);

    Pagination<CastMember> listCastMembers(final SearchQuery query);

    List<CastMemberID> existsByIds(final Iterable<CastMemberID> ids);
}
