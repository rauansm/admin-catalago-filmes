package com.codelabs.admin.catalago.domain.video;

import com.codelabs.admin.catalago.common.utils.IdUtils;
import com.codelabs.admin.catalago.domain.ValueObject;
import com.codelabs.admin.catalago.domain.enums.MediaStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class AudioVideoMedia extends ValueObject {

    private final String id;

    @EqualsAndHashCode.Include
    private final String checksum;

    private final String name;

    @EqualsAndHashCode.Include
    private final String rawLocation;

    private final String encodedLocation;

    private final MediaStatus status;

    private AudioVideoMedia(
            final String id,
            final String checksum,
            final String name,
            final String rawLocation,
            final String encodedLocation,
            final MediaStatus status
    ) {
        this.id = Objects.requireNonNull(id);
        this.checksum = Objects.requireNonNull(checksum);
        this.name = Objects.requireNonNull(name);
        this.rawLocation = Objects.requireNonNull(rawLocation);
        this.encodedLocation = Objects.requireNonNull(encodedLocation);
        this.status = Objects.requireNonNull(status);
    }

    public static AudioVideoMedia with(
            final String checksum,
            final String name,
            final String rawLocation
    ) {
        return new AudioVideoMedia(IdUtils.uuid(), checksum, name, rawLocation, "", MediaStatus.PENDING);
    }

    public static AudioVideoMedia with(
            final String id,
            final String checksum,
            final String name,
            final String rawLocation,
            final String encodedLocation,
            final MediaStatus status
    ) {
        return new AudioVideoMedia(id, checksum, name, rawLocation, encodedLocation, status);
    }
}
