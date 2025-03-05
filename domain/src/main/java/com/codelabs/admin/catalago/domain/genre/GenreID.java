package com.codelabs.admin.catalago.domain.genre;

import com.codelabs.admin.catalago.domain.Identifier;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Objects;
import java.util.UUID;

@EqualsAndHashCode(callSuper = false)
@ToString
public class GenreID extends Identifier {

    private final String value;

    private GenreID(final String value) {
        Objects.requireNonNull(value);
        this.value = value;
    }

    public static GenreID unique() {
        return GenreID.from(UUID.randomUUID());
    }

    public static GenreID from(final String id) {
        return new GenreID(id);
    }

    public static GenreID from(final UUID id) {
        return new GenreID(id.toString().toLowerCase());
    }

    @Override
    public String getValue() {
        return value;
    }

}
