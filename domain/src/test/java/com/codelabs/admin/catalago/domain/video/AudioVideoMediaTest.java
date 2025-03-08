package com.codelabs.admin.catalago.domain.video;

import com.codelabs.admin.catalago.common.utils.IdUtils;
import com.codelabs.admin.catalago.domain.enums.MediaStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AudioVideoMediaTest {

    @Test
    public void givenValidParams_whenCallsNewAudioVideo_ShouldReturnInstance() {
        // given
        final var expectedId = IdUtils.uuid();
        final var expectedChecksum = "abc";
        final var expectedName = "Banner.png";
        final var expectedRawLocation = "/images/ac";
        final var expectedEncodedLocation = "/images/ac-encoded";
        final var expectedStatus = MediaStatus.PENDING;

        // when
        final var actualVideo =
                AudioVideoMedia.with(expectedId, expectedChecksum, expectedName, expectedRawLocation, expectedEncodedLocation, expectedStatus);

        // then
        Assertions.assertNotNull(actualVideo);
        Assertions.assertEquals(expectedId, actualVideo.getId());
        Assertions.assertEquals(expectedChecksum, actualVideo.getChecksum());
        Assertions.assertEquals(expectedName, actualVideo.getName());
        Assertions.assertEquals(expectedRawLocation, actualVideo.getRawLocation());
        Assertions.assertEquals(expectedEncodedLocation, actualVideo.getEncodedLocation());
        Assertions.assertEquals(expectedStatus, actualVideo.getStatus());
    }

    @Test
    public void givenTwoVideosWithSameChecksumAndLocation_whenCallsEquals_ShouldReturnTrue() {
        // given
        final var expectedChecksum = "abc";
        final var expectedRawLocation = "/images/ac";

        final var img1 =
                AudioVideoMedia.with(expectedChecksum, "Random", expectedRawLocation);

        final var img2 =
                AudioVideoMedia.with(expectedChecksum, "Simple", expectedRawLocation);

        // then
        Assertions.assertEquals(img1, img2);
        Assertions.assertNotSame(img1, img2);
    }

    @Test
    public void givenInvalidParams_whenCallsWith_ShouldReturnError() {
        Assertions.assertThrows(
                NullPointerException.class,
                () -> AudioVideoMedia.with(null, "131", "Random", "/videos", "/videos", MediaStatus.PENDING)
        );

        Assertions.assertThrows(
                NullPointerException.class,
                () -> AudioVideoMedia.with("id", "abc", null, "/videos", "/videos", MediaStatus.PENDING)
        );

        Assertions.assertThrows(
                NullPointerException.class,
                () -> AudioVideoMedia.with("id", "abc", "Random", null, "/videos", MediaStatus.PENDING)
        );

        Assertions.assertThrows(
                NullPointerException.class,
                () -> AudioVideoMedia.with("id", "abc", "Random", "/videos", null, MediaStatus.PENDING)
        );

        Assertions.assertThrows(
                NullPointerException.class,
                () -> AudioVideoMedia.with("id", "abc", "Random", "/videos", "/videos", null)
        );
    }
}