package com.codelabs.admin.catalago.domain.video;

import com.codelabs.admin.catalago.domain.Identifier;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Objects;
import java.util.UUID;

@EqualsAndHashCode(callSuper = false)
@ToString
public class VideoID extends Identifier {

    private final String value;

    private VideoID(final String value) {
        this.value = Objects.requireNonNull(value);
    }

    public static VideoID from(final String id) {
        return new VideoID(id.toLowerCase());
    }

    public static VideoID from(final UUID id) {
        return VideoID.from(id.toString());
    }

    public static VideoID unique() {
        return VideoID.from(UUID.randomUUID());
    }

    @Override
    public String getValue() {
        return value;
    }

}
