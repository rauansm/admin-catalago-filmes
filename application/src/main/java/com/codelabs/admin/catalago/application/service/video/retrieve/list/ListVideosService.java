package com.codelabs.admin.catalago.application.service.video.retrieve.list;

import com.codelabs.admin.catalago.application.ports.in.ListVideosUseCase;
import com.codelabs.admin.catalago.application.ports.out.VideoPort;
import com.codelabs.admin.catalago.common.stereotype.UseCase;
import com.codelabs.admin.catalago.domain.pagination.Pagination;
import com.codelabs.admin.catalago.domain.video.VideoPreview;
import com.codelabs.admin.catalago.domain.video.VideoSearchQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static net.logstash.logback.marker.Markers.append;

@UseCase
@RequiredArgsConstructor
@Slf4j
public class ListVideosService implements ListVideosUseCase {

    private final VideoPort videoPort;

    @Override
    public Pagination<VideoPreview> listVideos(final VideoSearchQuery query) {
        log.info(append("query", query), "Search all video by query params");

        final var videos = this.videoPort.listVideos(query);
        log.info(append("videos", videos), "Found videos");

        return videos;
    }
}
