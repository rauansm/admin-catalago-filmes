package com.codelabs.admin.catalago.application.ports.in;

import com.codelabs.admin.catalago.application.service.video.update.UpdateVideoCommand;
import com.codelabs.admin.catalago.domain.video.Video;

public interface UpdateVideoUseCase {
    Video update(final UpdateVideoCommand command);
}
