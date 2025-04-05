package com.codelabs.admin.catalago.application.ports.in;

import com.codelabs.admin.catalago.domain.video.Video;

public interface GetVideoByIdUseCase {
    Video getById(final String id);
}
