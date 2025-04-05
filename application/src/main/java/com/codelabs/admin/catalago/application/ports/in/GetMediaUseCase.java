package com.codelabs.admin.catalago.application.ports.in;

import com.codelabs.admin.catalago.application.service.video.media.get.GetMediaCommand;
import com.codelabs.admin.catalago.domain.video.Resource;

public interface GetMediaUseCase {
    Resource getMedia (final GetMediaCommand command);
}
