package com.codelabs.admin.catalago.infrastructure.web.in.castmember.dto;

import com.codelabs.admin.catalago.domain.castmember.CastMember;
import com.codelabs.admin.catalago.domain.castmember.CastMemberID;

public record CastMemberResponse(
        String id
) {

    public static CastMemberResponse from(final CastMemberID id) {
        return new CastMemberResponse(id.getValue());
    }

    public static CastMemberResponse from(final CastMember member) {
        return from(member.getId());
    }
}
