package com.codelabs.admin.catalago.application.ports.out;

import com.codelabs.admin.catalago.domain.video.AudioVideoMedia;
import com.codelabs.admin.catalago.domain.video.ImageMedia;
import com.codelabs.admin.catalago.domain.video.VideoID;
import com.codelabs.admin.catalago.domain.video.VideoResource;

public interface MediaResourcePort {
    AudioVideoMedia storeAudioVideo(VideoID id, VideoResource resource);

    ImageMedia storeImage(VideoID id, VideoResource resource);

    void clearResources(VideoID id);
}
