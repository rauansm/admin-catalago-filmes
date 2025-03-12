package com.codelabs.admin.catalago.infrastructure.web.in.video.dto;

import com.codelabs.admin.catalago.common.utils.CollectionUtils;
import com.codelabs.admin.catalago.domain.Identifier;
import com.codelabs.admin.catalago.domain.video.AudioVideoMedia;
import com.codelabs.admin.catalago.domain.video.ImageMedia;
import com.codelabs.admin.catalago.domain.video.Video;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.Set;

public record VideoDetailsResponse(
        @JsonProperty("id") String id,
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("year_launched") int yearLaunched,
        @JsonProperty("duration") double duration,
        @JsonProperty("opened") boolean opened,
        @JsonProperty("published") boolean published,
        @JsonProperty("rating") String rating,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt,
        @JsonProperty("banner") ImageMediaResponse banner,
        @JsonProperty("thumbnail") ImageMediaResponse thumbnail,
        @JsonProperty("thumbnail_half") ImageMediaResponse thumbnailHalf,
        @JsonProperty("video") AudioVideoMediaResponse video,
        @JsonProperty("trailer") AudioVideoMediaResponse trailer,
        @JsonProperty("categories_id") Set<String> categoriesId,
        @JsonProperty("genres_id") Set<String> genresId,
        @JsonProperty("cast_members_id") Set<String> castMembersId
) {

    public static VideoDetailsResponse from(final Video video) {
        return new VideoDetailsResponse(
                video.getId().getValue(),
                video.getTitle(),
                video.getDescription(),
                video.getLaunchedAt().getValue(),
                video.getDuration(),
                video.getOpened(),
                video.getPublished(),
                video.getRating().getName(),
                video.getCreatedAt(),
                video.getUpdatedAt(),
                from(video.getBanner().orElse(null)),
                from(video.getThumbnail().orElse(null)),
                from(video.getThumbnailHalf().orElse(null)),
                from(video.getVideo().orElse(null)),
                from(video.getTrailer().orElse(null)),
                CollectionUtils.mapTo(video.getCategories(), Identifier::getValue),
                CollectionUtils.mapTo(video.getGenres(), Identifier::getValue),
                CollectionUtils.mapTo(video.getCastMembers(), Identifier::getValue)
        );
    }

    static AudioVideoMediaResponse from(final AudioVideoMedia media) {
        if (media == null) {
            return null;
        }
        return new AudioVideoMediaResponse(
                media.getId(),
                media.getChecksum(),
                media.getName(),
                media.getRawLocation(),
                media.getEncodedLocation(),
                media.getStatus().name()
        );
    }

    static ImageMediaResponse from(final ImageMedia image) {
        if (image == null) {
            return null;
        }
        return new ImageMediaResponse(
                image.getId(),
                image.getChecksum(),
                image.getName(),
                image.getLocation()
        );
    }

}
