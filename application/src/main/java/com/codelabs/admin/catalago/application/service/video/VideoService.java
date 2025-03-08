package com.codelabs.admin.catalago.application.service.video;

import com.codelabs.admin.catalago.application.ports.in.VideoUseCase;
import com.codelabs.admin.catalago.application.ports.out.*;
import com.codelabs.admin.catalago.common.exceptions.InternalErrorException;
import com.codelabs.admin.catalago.common.exceptions.NotFoundException;
import com.codelabs.admin.catalago.common.stereotype.UseCase;
import com.codelabs.admin.catalago.domain.Identifier;
import com.codelabs.admin.catalago.domain.castmember.CastMemberID;
import com.codelabs.admin.catalago.domain.category.CategoryID;
import com.codelabs.admin.catalago.domain.enums.Rating;
import com.codelabs.admin.catalago.domain.genre.GenreID;
import com.codelabs.admin.catalago.domain.video.Video;
import com.codelabs.admin.catalago.domain.video.VideoResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.codelabs.admin.catalago.domain.enums.VideoMediaType.*;
import static net.logstash.logback.marker.Markers.append;

@UseCase
@RequiredArgsConstructor
@Slf4j
public class VideoService implements VideoUseCase {

    private final CategoryPort categoryPort;
    private final CastMemberPort castMemberPort;
    private final GenrePort genrePort;
    private final VideoPort videoPort;
    private final MediaResourcePort mediaResourcePort;

    @Override
    public Video create(final CreateVideoCommand command) {
        log.info(append("command", command), "Starting video creation service");

        final var categories = toIdentifier(command.categories(), CategoryID::from);
        final var genres = toIdentifier(command.genres(), GenreID::from);
        final var members = toIdentifier(command.members(), CastMemberID::from);

        validateCategories(categories);
        validateGenres(genres);
        validateMembers(members);

        final var video = Video.newVideo(
                command.title(),
                command.description(),
                Year.of(command.launchedAt()),
                command.duration(),
                command.opened(),
                command.published(),
                Rating.entryOf(command.rating()),
                categories,
                genres,
                members
        );

        log.info("Genre creation service completed successfully.");
        return storeMediaFiles(command, video);
    }

    private Video storeMediaFiles(final CreateVideoCommand command, final Video video) {
        log.info("Starting cloud media storage. {}", video.getId().getValue());

        final var id = video.getId();

        try {

            final var videoMedia = command.getVideo()
                    .map(it -> this.mediaResourcePort.storeAudioVideo(id, VideoResource.with(VIDEO, it)))
                    .orElse(null);

            final var trailerMedia = command.getTrailer()
                    .map(it -> this.mediaResourcePort.storeAudioVideo(id, VideoResource.with(TRAILER, it)))
                    .orElse(null);

            final var bannerMedia = command.getBanner()
                    .map(it -> this.mediaResourcePort.storeImage(id, VideoResource.with(BANNER, it)))
                    .orElse(null);

            final var thumbnailMedia = command.getThumbnail()
                    .map(it -> this.mediaResourcePort.storeImage(id, VideoResource.with(THUMBNAIL, it)))
                    .orElse(null);

            final var thumbHalfMedia = command.getThumbnailHalf()
                    .map(it -> this.mediaResourcePort.storeImage(id, VideoResource.with(THUMBNAIL_HALF, it)))
                    .orElse(null);

            log.info("cloud media storage completed successfully. {}", id);

            return videoPort.save(video
                    .setVideo(videoMedia)
                    .setTrailer(trailerMedia)
                    .setBanner(bannerMedia)
                    .setThumbnail(thumbnailMedia)
                    .setThumbnailHalf(thumbHalfMedia));

        } catch (final Throwable t) {
            this.mediaResourcePort.clearResources(id);

            log.error("error on create video [videoId:%s]".formatted(id), t);
            throw new InternalErrorException("An error on create video was observed [videoId:%s]".formatted(id.getValue()));
        }

    }

    private void validateCategories(final Set<CategoryID> ids) {
        log.info("starting category validation {}", ids);
        validateAggregate("categories", ids, categoryPort::existsByIds);
    }

    private void validateGenres(final Set<GenreID> ids) {
        log.info("starting genre validation {}", ids);
        validateAggregate("genres", ids, genrePort::existsByIds);
    }

    private void validateMembers(final Set<CastMemberID> ids) {
        log.info("starting cast member validation {}", ids);
        validateAggregate("cast members", ids, castMemberPort::existsByIds);
    }

    private <T extends Identifier> void validateAggregate(final String aggregate, final Set<T> ids,
                                                          final Function<Iterable<T>, List<T>> existsByIds) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        final var retrievedIds = existsByIds.apply(ids);
        log.info("%s ids retrieved from database {}".formatted(aggregate), ids);

        if (ids.size() != retrievedIds.size()) {
            final var missingIds = new ArrayList<>(ids);
            missingIds.removeAll(retrievedIds);

            final var missingIdsMessage = missingIds.stream()
                    .map(Identifier::getValue)
                    .collect(Collectors.joining(", "));

            throw new NotFoundException("Some %s could not be found: %s".formatted(aggregate, missingIdsMessage));
        }
    }

    private <T> Set<T> toIdentifier(final Set<String> ids, final Function<String, T> mapper) {
        return ids.stream()
                .map(mapper)
                .collect(Collectors.toSet());
    }
}
