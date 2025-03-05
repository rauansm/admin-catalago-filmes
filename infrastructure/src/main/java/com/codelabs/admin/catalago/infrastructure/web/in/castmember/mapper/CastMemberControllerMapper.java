package com.codelabs.admin.catalago.infrastructure.web.in.castmember.mapper;

import com.codelabs.admin.catalago.domain.castmember.CastMember;
import com.codelabs.admin.catalago.infrastructure.web.in.castmember.dto.CastMemberRequest;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class CastMemberControllerMapper {

    public CastMember toDomain(final CastMemberRequest request) {
        return CastMember.newMember(request.name(), request.type());
    }

    public CastMember toDomain(final String id, final CastMemberRequest request) {
        return CastMember.newMember(id, request.name(), request.type());
    }

    public <T> T toResponse(final CastMember member, Function<CastMember, T> mapper) {
        return mapper.apply(member);
    }

}
