package com.codelabs.admin.catalago.infrastructure.persistence.video.entity;

import com.codelabs.admin.catalago.domain.genre.GenreID;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@Entity(name = "VideoGenre")
@Table(name = "videos_genres")
public class VideoGenreEntity {

    @EmbeddedId
    private VideoGenreID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("videoId")
    private VideoEntity video;

    public static VideoGenreEntity from(final VideoEntity video, final GenreID genre) {
        return new VideoGenreEntity(
                VideoGenreID.from(video.getId(), genre.getValue()),
                video
        );
    }
}
