package com.codelabs.admin.catalago.application.ports.in;

import com.codelabs.admin.catalago.application.service.video.create.CreateVideoCommand;
import com.codelabs.admin.catalago.domain.video.Video;

public interface CreateVideoUseCase {
    Video create(final CreateVideoCommand command);
}
