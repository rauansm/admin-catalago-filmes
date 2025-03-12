package com.codelabs.admin.catalago.infrastructure.persistence.video.adapter;

import com.codelabs.admin.catalago.application.ports.out.VideoPort;
import com.codelabs.admin.catalago.common.exceptions.NotFoundException;
import com.codelabs.admin.catalago.common.stereotype.PersistenceAdapter;
import com.codelabs.admin.catalago.common.utils.SqlUtils;
import com.codelabs.admin.catalago.domain.Identifier;
import com.codelabs.admin.catalago.domain.pagination.Pagination;
import com.codelabs.admin.catalago.domain.video.Video;
import com.codelabs.admin.catalago.domain.video.VideoID;
import com.codelabs.admin.catalago.domain.video.VideoPreview;
import com.codelabs.admin.catalago.domain.video.VideoSearchQuery;
import com.codelabs.admin.catalago.infrastructure.persistence.video.entity.VideoEntity;
import com.codelabs.admin.catalago.infrastructure.persistence.video.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.codelabs.admin.catalago.common.utils.CollectionUtils.mapTo;
import static com.codelabs.admin.catalago.common.utils.CollectionUtils.nullIfEmpty;
import static net.logstash.logback.argument.StructuredArguments.kv;
import static net.logstash.logback.marker.Markers.append;

@PersistenceAdapter
@RequiredArgsConstructor
@Slf4j
public class VideoPersistenceAdapter implements VideoPort {

    private final VideoRepository videoRepository;

    @Override
    @Transactional
    public Video save(final Video video) {
        log.info(append("video", video), "Starting video persistence in the database...");

        final VideoEntity entity = VideoEntity.from(video);
        log.info(append("entity", entity), "Object mapped successfully!");

        final VideoEntity savedEntity = this.videoRepository.save(entity);

        log.info(append("entity", savedEntity), "Video persisted successfully!");
        return savedEntity.toAggregate();
    }

    @Override
    @Transactional(readOnly = true)
    public Video getById(final VideoID id) {
        log.info("Searching video in the database... {}", id.getValue());

        final Optional<VideoEntity> genreEntity = this.videoRepository.findById(id.getValue());

        genreEntity.ifPresent(
                entity -> log.info(append("entity", entity), "Video found successfully!"));

        final Video video = genreEntity
                .map(VideoEntity::toAggregate)
                .orElseThrow(() -> new NotFoundException(String.format("Video not found in database with id %s", id.getValue())));

        log.info(append("video", video), "Entity to domain mapping done!");
        return video;
    }

    @Override
    public void deleteById(final VideoID id) {
        log.info("Starting video deletion in the database... {}", id.getValue());

        final String idValue = id.getValue();
        if (this.videoRepository.existsById(idValue)) {
            this.videoRepository.deleteById(idValue);
            log.info("Video deleted successfully! {}", idValue);
        }
    }

    @Override
    public Pagination<VideoPreview> listVideos(final VideoSearchQuery query) {
        log.info(append("params", query), "Searching video in database by parameters");

        final var page = PageRequest.of(
                query.page(),
                query.perPage(),
                Sort.by(Sort.Direction.fromString(query.direction()), query.sort())
        );

        final var actualPage = this.videoRepository.findAll(
                SqlUtils.like(SqlUtils.upper(query.terms())),
                nullIfEmpty(mapTo(query.castMembers(), Identifier::getValue)),
                nullIfEmpty(mapTo(query.categories(), Identifier::getValue)),
                nullIfEmpty(mapTo(query.genres(), Identifier::getValue)),
                page);
        log.info("were found {} videos", kv("videos_size", actualPage.getTotalElements()));

        return new Pagination<>(
                actualPage.getNumber(),
                actualPage.getSize(),
                actualPage.getTotalElements(),
                actualPage.toList()
        );
    }
}
