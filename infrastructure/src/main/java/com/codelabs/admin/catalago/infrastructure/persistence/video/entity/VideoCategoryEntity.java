package com.codelabs.admin.catalago.infrastructure.persistence.video.entity;

import com.codelabs.admin.catalago.domain.category.CategoryID;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@Entity(name = "VideoCategory")
@Table(name = "videos_categories")
public class VideoCategoryEntity {

    @EmbeddedId
    private VideoCategoryID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("videoId")
    private VideoEntity video;

    public static VideoCategoryEntity from(final VideoEntity video, final CategoryID category) {
        return new VideoCategoryEntity(
                VideoCategoryID.from(video.getId(), category.getValue()),
                video
        );
    }

}
