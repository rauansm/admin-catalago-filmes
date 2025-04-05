package com.codelabs.admin.catalago.application.service.video.media.get;

import com.codelabs.admin.catalago.application.ports.in.GetMediaUseCase;
import com.codelabs.admin.catalago.application.ports.out.MediaResourcePort;
import com.codelabs.admin.catalago.common.stereotype.UseCase;
import com.codelabs.admin.catalago.domain.enums.VideoMediaType;
import com.codelabs.admin.catalago.domain.video.Resource;
import com.codelabs.admin.catalago.domain.video.VideoID;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class GetMediaService implements GetMediaUseCase {

    private final MediaResourcePort mediaResourcePort;

    @Override
    public Resource getMedia(final GetMediaCommand command) {

        final var id = VideoID.from(command.videoId());
        final var type = VideoMediaType.entryOf(command.mediaType());

        final var resource =
                this.mediaResourcePort.getResource(id, type);

        return resource;
    }
}
