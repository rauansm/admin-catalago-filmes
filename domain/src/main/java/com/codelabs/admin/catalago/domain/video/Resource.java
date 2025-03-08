package com.codelabs.admin.catalago.domain.video;

import com.codelabs.admin.catalago.domain.ValueObject;
import lombok.Getter;

import java.util.Objects;

@Getter
public class Resource extends ValueObject {

    private final byte[] content;
    private final String checksum;
    private final String contentType;
    private final String name;

    private Resource(final byte[] content, final String checksum, final String contentType, final String name) {
        this.content = Objects.requireNonNull(content);
        this.checksum = Objects.requireNonNull(checksum);
        this.contentType = Objects.requireNonNull(contentType);
        this.name = Objects.requireNonNull(name);
    }

    public static Resource with(final byte[] content, final String checksum, final String contentType, final String name) {
        return new Resource(content, checksum, contentType, name);
    }
}
