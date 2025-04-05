package com.codelabs.admin.catalago.infrastructure.web.in.video;

import com.codelabs.admin.catalago.application.ports.in.*;
import com.codelabs.admin.catalago.application.service.video.create.CreateVideoCommand;
import com.codelabs.admin.catalago.application.service.video.media.get.GetMediaCommand;
import com.codelabs.admin.catalago.application.service.video.update.UpdateVideoCommand;
import com.codelabs.admin.catalago.common.utils.HashingUtils;
import com.codelabs.admin.catalago.domain.castmember.CastMemberID;
import com.codelabs.admin.catalago.domain.category.CategoryID;
import com.codelabs.admin.catalago.domain.genre.GenreID;
import com.codelabs.admin.catalago.domain.pagination.Pagination;
import com.codelabs.admin.catalago.domain.video.Resource;
import com.codelabs.admin.catalago.domain.video.VideoSearchQuery;
import com.codelabs.admin.catalago.infrastructure.web.in.video.dto.VideoDetailsResponse;
import com.codelabs.admin.catalago.infrastructure.web.in.video.dto.VideoListResponse;
import com.codelabs.admin.catalago.infrastructure.web.in.video.dto.VideoRequest;
import com.codelabs.admin.catalago.infrastructure.web.in.video.dto.VideoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.Set;

import static com.codelabs.admin.catalago.common.utils.CollectionUtils.mapTo;

@RestController
@RequiredArgsConstructor
public class VideoController implements VideoAPI {

    private final CreateVideoUseCase createVideoUseCase;
    private final GetVideoByIdUseCase getVideoByIdUseCase;
    private final UpdateVideoUseCase updateVideoUseCase;
    private final DeleteVideoUseCase deleteVideoUseCase;
    private final ListVideosUseCase listVideosUseCase;
    private final GetMediaUseCase getMediaUseCase;


    @Override
    public Pagination<VideoListResponse> list(
            final String search,
            final int page,
            final int perPage,
            final String sort,
            final String direction,
            final Set<String> castMembers,
            final Set<String> categories,
            final Set<String> genres
    ) {
        final var castMemberIDs = mapTo(castMembers, CastMemberID::from);
        final var categoriesIDs = mapTo(categories, CategoryID::from);
        final var genresIDs = mapTo(genres, GenreID::from);

        final var query =
                new VideoSearchQuery(page, perPage, search, sort, direction, castMemberIDs, categoriesIDs, genresIDs);

        final var listVideos = listVideosUseCase.listVideos(query);

        return listVideos.map(VideoListResponse::from);
    }

    @Override
    public ResponseEntity<?> createFull(
            final String title,
            final String description,
            final Integer launchedAt,
            final Double duration,
            final Boolean wasOpened,
            final Boolean wasPublished,
            final String rating,
            final Set<String> categories,
            final Set<String> castMembers,
            final Set<String> genres,
            final MultipartFile videoFile,
            final MultipartFile trailerFile,
            final MultipartFile bannerFile,
            final MultipartFile thumbFile,
            final MultipartFile thumbHalfFile
    ) {
        final var command = CreateVideoCommand.with(
                title,
                description,
                launchedAt,
                duration,
                wasOpened,
                wasPublished,
                rating,
                categories,
                genres,
                castMembers,
                resourceOf(videoFile),
                resourceOf(trailerFile),
                resourceOf(bannerFile),
                resourceOf(thumbFile),
                resourceOf(thumbHalfFile)
        );

        final var video = this.createVideoUseCase.create(command);


        return ResponseEntity.created(URI.create("/videos/" + video.getId())).body(VideoResponse.from(video));
    }

    @Override
    public ResponseEntity<?> createPartial(final VideoRequest request) {
        final var command = CreateVideoCommand.with(
                request.title(),
                request.description(),
                request.yearLaunched(),
                request.duration(),
                request.opened(),
                request.published(),
                request.rating(),
                request.categories(),
                request.genres(),
                request.castMembers()
        );

        final var video = this.createVideoUseCase.create(command);


        return ResponseEntity.created(URI.create("/videos/" + video.getId())).body(VideoResponse.from(video));
    }

    @Override
    public VideoDetailsResponse getById(final String id) {
        return VideoDetailsResponse.from(this.getVideoByIdUseCase.getById(id));
    }

    @Override
    public ResponseEntity<?> update(final String id, final VideoRequest payload) {
        final var command = UpdateVideoCommand.with(
                id,
                payload.title(),
                payload.description(),
                payload.yearLaunched(),
                payload.duration(),
                payload.opened(),
                payload.published(),
                payload.rating(),
                payload.categories(),
                payload.genres(),
                payload.castMembers()
        );

        final var video = this.updateVideoUseCase.update(command);

        return ResponseEntity.ok()
                .location(URI.create("/videos/" + video.getId()))
                .body(VideoResponse.from(video));
    }

    @Override
    public void deleteById(final String id) {
        this.deleteVideoUseCase.deleteById(id);
    }

    @Override
    public ResponseEntity<InputStreamResource> getMediaByType(final String id, final String type) {
        final var aMedia =
                this.getMediaUseCase.getMedia(GetMediaCommand.with(id, type));

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(aMedia.getContentType()))
                .contentLength(aMedia.getContentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=%s".formatted(aMedia.getName()))
                .body(new InputStreamResource(aMedia.getInputStream()));
    }


    private Resource resourceOf(final MultipartFile part) {
        if (part == null) {
            return null;
        }

        try {
            return Resource.with(
                    part.getInputStream(),
                    part.getBytes().length,
                    HashingUtils.checksum(part.getBytes()),
                    part.getContentType(),
                    part.getOriginalFilename()
            );
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}