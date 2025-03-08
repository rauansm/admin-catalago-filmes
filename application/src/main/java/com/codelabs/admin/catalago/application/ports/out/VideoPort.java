package com.codelabs.admin.catalago.application.ports.out;

import com.codelabs.admin.catalago.domain.pagination.Pagination;
import com.codelabs.admin.catalago.domain.video.Video;
import com.codelabs.admin.catalago.domain.video.VideoID;
import com.codelabs.admin.catalago.domain.video.VideoSearchQuery;

public interface VideoPort {
    Video save(final Video video);

    Video getById(final VideoID id);

    void deleteById(final VideoID id);

    Pagination<Video> listVideos(final VideoSearchQuery query);
}
