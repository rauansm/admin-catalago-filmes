package com.codelabs.admin.catalago.domain.video;

import com.codelabs.admin.catalago.domain.ValueObject;
import lombok.Getter;

import java.io.InputStream;
import java.util.Objects;

@Getter
public class Resource extends ValueObject {

    private final InputStream inputStream;
    private final long contentLength;
    private final String checksum;
    private final String contentType;
    private final String name;

    private Resource(final InputStream inputStream, final long contentLength, final String checksum, final String contentType, final String name) {
        this.inputStream = Objects.requireNonNull(inputStream);
        this.contentLength = contentLength;
        this.checksum = Objects.requireNonNull(checksum);
        this.contentType = Objects.requireNonNull(contentType);
        this.name = Objects.requireNonNull(name);
    }

    public static Resource with(final InputStream inputStream, final long contentLength, final String checksum, final String contentType, final String name) {
        return new Resource(inputStream, contentLength, checksum, contentType, name);
    }
}