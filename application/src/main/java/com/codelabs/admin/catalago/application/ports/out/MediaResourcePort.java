package com.codelabs.admin.catalago.application.ports.out;

import com.codelabs.admin.catalago.domain.enums.VideoMediaType;
import com.codelabs.admin.catalago.domain.video.*;

public interface MediaResourcePort {
    AudioVideoMedia storeAudioVideo(VideoID id, VideoResource resource);

    ImageMedia storeImage(VideoID id, VideoResource resource);

    Resource getResource(VideoID id, VideoMediaType type);

    void clearResources(VideoID id);
}
