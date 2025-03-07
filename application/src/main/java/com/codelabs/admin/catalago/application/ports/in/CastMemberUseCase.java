package com.codelabs.admin.catalago.application.ports.in;

import com.codelabs.admin.catalago.domain.castmember.CastMember;
import com.codelabs.admin.catalago.domain.category.Category;
import com.codelabs.admin.catalago.domain.pagination.Pagination;
import com.codelabs.admin.catalago.domain.pagination.SearchQuery;

public interface CastMemberUseCase {
    CastMember create(final CastMember castMember);

    void deleteById(final String id);

    CastMember getById(final String id);

    CastMember update(final CastMember castMember);

    Pagination<CastMember> listCastMembers(final SearchQuery query);
}
