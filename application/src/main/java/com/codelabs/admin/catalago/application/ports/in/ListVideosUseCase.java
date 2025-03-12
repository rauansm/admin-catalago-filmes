package com.codelabs.admin.catalago.application.ports.in;

import com.codelabs.admin.catalago.domain.pagination.Pagination;
import com.codelabs.admin.catalago.domain.video.VideoPreview;
import com.codelabs.admin.catalago.domain.video.VideoSearchQuery;

public interface ListVideosUseCase {
    Pagination<VideoPreview> listVideos(final VideoSearchQuery query);
}
