package com.codelabs.admin.catalago.infrastructure.web.in.castmember.dto;

import com.codelabs.admin.catalago.domain.castmember.CastMember;

public record CastMemberListResponse(
        String id,
        String name,
        String type,
        String createdAt
) {

    public static CastMemberListResponse from(final CastMember member) {
        return new CastMemberListResponse(
                member.getId().getValue(),
                member.getName(),
                member.getType().name(),
                member.getCreatedAt().toString());
    }
}
