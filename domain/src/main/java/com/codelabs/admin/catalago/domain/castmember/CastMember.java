package com.codelabs.admin.catalago.domain.castmember;

import com.codelabs.admin.catalago.common.utils.InstantUtils;
import com.codelabs.admin.catalago.domain.AggregateRoot;
import com.codelabs.admin.catalago.domain.enums.CastMemberType;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

@Getter
@ToString
public class CastMember extends AggregateRoot<CastMemberID> {

    private String name;
    private CastMemberType type;
    private Instant createdAt;
    private Instant updatedAt;

    protected CastMember(
            final CastMemberID id,
            final String name,
            final CastMemberType type,
            final Instant creationDate,
            final Instant updateDate
    ) {
        super(id);
        this.name = name;
        this.type = type;
        this.createdAt = creationDate;
        this.updatedAt = updateDate;
    }

    public static CastMember newMember(final String name, final CastMemberType type) {
        final var id = CastMemberID.unique();
        final var now = InstantUtils.now();
        return new CastMember(id, name, type, now, now);
    }

    public static CastMember newMember(final String id, final String name, final CastMemberType type) {
        final var castMemberID = CastMemberID.from(id);
        final var now = InstantUtils.now();
        return new CastMember(castMemberID, name, type, now, now);
    }

    public static CastMember with(final CastMember member) {
        return new CastMember(
                member.id,
                member.name,
                member.type,
                member.createdAt,
                member.updatedAt
        );
    }

    public static CastMember with(
            final CastMemberID anId,
            final String aName,
            final CastMemberType aType,
            final Instant aCreationDate,
            final Instant aUpdateDate
    ) {
        return new CastMember(anId, aName, aType, aCreationDate, aUpdateDate);
    }

    public CastMember update(final String name, final CastMemberType type) {
        this.name = name;
        this.type = type;
        this.updatedAt = InstantUtils.now();
        return this;
    }
}
