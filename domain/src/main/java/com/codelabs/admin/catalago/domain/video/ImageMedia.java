package com.codelabs.admin.catalago.domain.video;

import com.codelabs.admin.catalago.domain.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class ImageMedia extends ValueObject {

    @EqualsAndHashCode.Include
    private final String checksum;

    private final String name;

    @EqualsAndHashCode.Include
    private final String location;

    private ImageMedia(final String checksum, final String name, final String location) {
        this.checksum = Objects.requireNonNull(checksum);
        this.name = Objects.requireNonNull(name);
        this.location = Objects.requireNonNull(location);
    }

    public static ImageMedia with(final String checksum, final String name, final String location) {
        return new ImageMedia(checksum, name, location);
    }
}
