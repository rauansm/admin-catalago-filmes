package com.codelabs.admin.catalago.infrastructure.web.in.castmember.dto;

import com.codelabs.admin.catalago.domain.castmember.CastMember;

public record CastMemberDetailsResponse(
        String id,
        String name,
        String type,
        String createdAt,
        String updatedAt
) {

    static public CastMemberDetailsResponse from(final CastMember member) {
        return new CastMemberDetailsResponse(
                member.getId().getValue(),
                member.getName(),
                member.getType().name(),
                member.getCreatedAt().toString(),
                member.getUpdatedAt().toString()
        );
    }

}
