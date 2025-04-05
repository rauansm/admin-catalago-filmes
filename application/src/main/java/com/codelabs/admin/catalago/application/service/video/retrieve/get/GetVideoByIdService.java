package com.codelabs.admin.catalago.application.service.video.retrieve.get;

import com.codelabs.admin.catalago.application.ports.in.GetVideoByIdUseCase;
import com.codelabs.admin.catalago.application.ports.out.VideoPort;
import com.codelabs.admin.catalago.common.stereotype.UseCase;
import com.codelabs.admin.catalago.domain.video.Video;
import com.codelabs.admin.catalago.domain.video.VideoID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static net.logstash.logback.marker.Markers.append;

@UseCase
@RequiredArgsConstructor
@Slf4j
public class GetVideoByIdService implements GetVideoByIdUseCase {

    private final VideoPort videoPort;

    @Override
    public Video getById(final String id) {
        log.info(append("id", id), "Search video by id");

        final var video = this.videoPort.getById(VideoID.from(id));
        log.info(append("video", video), "Video successfully found.");

        return video;
    }
}
