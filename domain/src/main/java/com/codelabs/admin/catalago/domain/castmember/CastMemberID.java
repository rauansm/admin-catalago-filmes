package com.codelabs.admin.catalago.domain.castmember;

import com.codelabs.admin.catalago.domain.Identifier;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Objects;
import java.util.UUID;

@EqualsAndHashCode(callSuper = false)
@ToString
public class CastMemberID extends Identifier {

    private final String value;

    private CastMemberID(final String id) {
        Objects.requireNonNull(id);
        this.value = id;
    }

    public static CastMemberID unique() {
        return CastMemberID.from(UUID.randomUUID());
    }

    public static CastMemberID from(final String id) {
        return new CastMemberID(id);
    }

    public static CastMemberID from(final UUID id) {
        return new CastMemberID(id.toString().toLowerCase());
    }

    @Override
    public String getValue() {
        return this.value;
    }
}
