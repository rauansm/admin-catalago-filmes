package com.codelabs.admin.catalago.application.service.video.create;

import com.codelabs.admin.catalago.application.ports.out.*;
import com.codelabs.admin.catalago.application.service.Fixture;
import com.codelabs.admin.catalago.domain.Identifier;
import com.codelabs.admin.catalago.domain.enums.VideoMediaType;
import com.codelabs.admin.catalago.domain.video.AudioVideoMedia;
import com.codelabs.admin.catalago.domain.video.ImageMedia;
import com.codelabs.admin.catalago.domain.video.Resource;
import com.codelabs.admin.catalago.domain.video.VideoResource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

public class CreateVideoSerivceTest {

    private CreateVideoService service;
    private CategoryPort categoryPort;
    private GenrePort genrePort;
    private VideoPort videoPort;
    private CastMemberPort castMemberPort;
    private MediaResourcePort mediaResourcePort;

    @BeforeEach
    void setup() {
        this.categoryPort = mock(CategoryPort.class);
        this.genrePort = mock(GenrePort.class);
        this.videoPort = mock(VideoPort.class);
        this.castMemberPort = mock(CastMemberPort.class);
        this.mediaResourcePort = mock(MediaResourcePort.class);
        this.service = new CreateVideoService(this.categoryPort, this.castMemberPort,
                this.genrePort, this.videoPort, this.mediaResourcePort);
    }

    @Test
    public void givenAValidCommand_whenCallsCreateVideo_shouldReturnVideoId() {
        // given
        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchYear = Year.of(Fixture.year());
        final var expectedDuration = Fixture.duration();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedCategories = Set.of(Fixture.Categories.filmes().getId());
        final var expectedGenres = Set.of(Fixture.Genres.acao().getId());
        final var expectedMembers = Set.of(
                Fixture.CastMembers.vinDisel().getId(),
                Fixture.CastMembers.rauan().getId()
        );
        final Resource expectedVideo = Fixture.Videos.resource(VideoMediaType.VIDEO);
        final Resource expectedTrailer = Fixture.Videos.resource(VideoMediaType.TRAILER);
        final Resource expectedBanner = Fixture.Videos.resource(VideoMediaType.BANNER);
        final Resource expectedThumb = Fixture.Videos.resource(VideoMediaType.THUMBNAIL);
        final Resource expectedThumbHalf = Fixture.Videos.resource(VideoMediaType.THUMBNAIL_HALF);

        final var command = CreateVideoCommand.with(
                expectedTitle,
                expectedDescription,
                expectedLaunchYear.getValue(),
                expectedDuration,
                expectedOpened,
                expectedPublished,
                expectedRating.getName(),
                asString(expectedCategories),
                asString(expectedGenres),
                asString(expectedMembers),
                expectedVideo,
                expectedTrailer,
                expectedBanner,
                expectedThumb,
                expectedThumbHalf
        );

        when(categoryPort.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedCategories));

        when(castMemberPort.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedMembers));

        when(genrePort.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedGenres));

        mockImageMedia();
        mockAudioVideoMedia();

        when(videoPort.save(any()))
                .thenAnswer(returnsFirstArg());

        // when
        final var actualResult = service.create(command);

        // then
        Assertions.assertNotNull(actualResult);
        Assertions.assertNotNull(actualResult.getId());

        verify(videoPort).save(argThat(actualVideo ->
                Objects.equals(expectedTitle, actualVideo.getTitle())
                        && Objects.equals(expectedDescription, actualVideo.getDescription())
                        && Objects.equals(expectedLaunchYear, actualVideo.getLaunchedAt())
                        && Objects.equals(expectedDuration, actualVideo.getDuration())
                        && Objects.equals(expectedOpened, actualVideo.getOpened())
                        && Objects.equals(expectedPublished, actualVideo.getPublished())
                        && Objects.equals(expectedRating, actualVideo.getRating())
                        && Objects.equals(expectedCategories, actualVideo.getCategories())
                        && Objects.equals(expectedGenres, actualVideo.getGenres())
                        && Objects.equals(expectedMembers, actualVideo.getCastMembers())
                        && Objects.equals(expectedVideo.getName(), actualVideo.getVideo().get().getName())
                        && Objects.equals(expectedTrailer.getName(), actualVideo.getTrailer().get().getName())
                        && Objects.equals(expectedBanner.getName(), actualVideo.getBanner().get().getName())
                        && Objects.equals(expectedThumb.getName(), actualVideo.getThumbnail().get().getName())
                        && Objects.equals(expectedThumbHalf.getName(), actualVideo.getThumbnailHalf().get().getName())
        ));
    }

    protected Set<String> asString(final Set<? extends Identifier> ids) {
        return ids.stream()
                .map(Identifier::getValue)
                .collect(Collectors.toSet());
    }

    private void mockImageMedia() {
        when(mediaResourcePort.storeImage(any(), any())).thenAnswer(t -> {
            final var videoResource = t.getArgument(1, VideoResource.class);
            final var resource = videoResource.resource();
            return ImageMedia.with(resource.getChecksum(), resource.getName(), "/img");
        });
    }

    private void mockAudioVideoMedia() {
        when(mediaResourcePort.storeAudioVideo(any(), any())).thenAnswer(t -> {
            final var videoResource = t.getArgument(1, VideoResource.class);
            final var resource = videoResource.resource();
            return AudioVideoMedia.with(
                    resource.getChecksum(),
                    resource.getName(),
                    "/img"
            );
        });
    }
}
