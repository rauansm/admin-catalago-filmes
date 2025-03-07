package com.codelabs.admin.catalago.infrastructure.web.in.castmember.dto;

import com.codelabs.admin.catalago.domain.enums.CastMemberType;

public record CastMemberRequest(String name, CastMemberType type) {
}
