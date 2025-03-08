package com.codelabs.admin.catalago.application.ports.in;

import com.codelabs.admin.catalago.application.service.video.CreateVideoCommand;
import com.codelabs.admin.catalago.domain.video.Video;

public interface VideoUseCase {
    Video create(final CreateVideoCommand command);
}
