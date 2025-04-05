package com.codelabs.admin.catalago.domain.enums;

import com.codelabs.admin.catalago.common.exceptions.DomainException;

import java.util.Arrays;

public enum VideoMediaType {
    VIDEO,
    TRAILER,
    BANNER,
    THUMBNAIL,
    THUMBNAIL_HALF;

    public static VideoMediaType entryOf(final String value) {
        return Arrays.stream(values())
                .filter(type -> type.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new DomainException(String.format("VideoMediaType %s Unknown.", value)));
    }
}
