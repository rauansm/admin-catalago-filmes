package com.codelabs.admin.catalago.application.service.video.delete;

import com.codelabs.admin.catalago.application.ports.in.DeleteVideoUseCase;
import com.codelabs.admin.catalago.application.ports.out.MediaResourcePort;
import com.codelabs.admin.catalago.application.ports.out.VideoPort;
import com.codelabs.admin.catalago.common.stereotype.UseCase;
import com.codelabs.admin.catalago.domain.video.VideoID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static net.logstash.logback.marker.Markers.append;

@UseCase
@RequiredArgsConstructor
@Slf4j
public class DeleteVideoService implements DeleteVideoUseCase {

    private final VideoPort videoPort;
    private final MediaResourcePort mediaResourcePort;

    @Override
    public void deleteById(final String id) {
        log.info(append("id", id), "delete video by id");

        final var videoId = VideoID.from(id);

        this.videoPort.deleteById(videoId);
        this.mediaResourcePort.clearResources(videoId);

        log.info("Delete video service completed successfully.");
    }

}
