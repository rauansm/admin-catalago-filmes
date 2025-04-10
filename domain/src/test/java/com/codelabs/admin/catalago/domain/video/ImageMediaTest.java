package com.codelabs.admin.catalago.domain.video;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ImageMediaTest {

    @Test
    public void givenValidParams_whenCallsNewImage_ShouldReturnInstance() {
        // given
        final var expectedChecksum = "abc";
        final var expectedName = "Banner.png";
        final var expectedLocation = "/images/ac";

        // when
        final var actualImage =
                ImageMedia.with(expectedChecksum, expectedName, expectedLocation);

        // then
        Assertions.assertNotNull(actualImage);
        Assertions.assertEquals(expectedChecksum, actualImage.getChecksum());
        Assertions.assertEquals(expectedName, actualImage.getName());
        Assertions.assertEquals(expectedLocation, actualImage.getLocation());
    }

    @Test
    public void givenTwoImagesWithSameChecksumAndLocation_whenCallsEquals_ShouldReturnTrue() {
        // given
        final var expectedChecksum = "abc";
        final var expectedLocation = "/images/ac";

        final var img1 =
                ImageMedia.with(expectedChecksum, "Random", expectedLocation);

        final var img2 =
                ImageMedia.with(expectedChecksum, "Simple", expectedLocation);

        // then
        Assertions.assertEquals(img1, img2);
        Assertions.assertNotSame(img1, img2);
    }

    @Test
    public void givenInvalidParams_whenCallsWith_ShouldReturnError() {
        Assertions.assertThrows(
                NullPointerException.class,
                () -> ImageMedia.with(null, "Random", "/images")
        );

        Assertions.assertThrows(
                NullPointerException.class,
                () -> ImageMedia.with("abc", null, "/images")
        );

        Assertions.assertThrows(
                NullPointerException.class,
                () -> ImageMedia.with("abc", "Random", null)
        );
    }
}