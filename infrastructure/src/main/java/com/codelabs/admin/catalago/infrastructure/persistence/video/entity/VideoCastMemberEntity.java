package com.codelabs.admin.catalago.infrastructure.persistence.video.entity;

import com.codelabs.admin.catalago.domain.castmember.CastMemberID;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@Entity(name = "VideoCastMember")
@Table(name = "videos_cast_members")
public class VideoCastMemberEntity {

    @EmbeddedId
    private VideoCastMemberID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("videoId")
    private VideoEntity video;

    public static VideoCastMemberEntity from(final VideoEntity entity, final CastMemberID castMemberID) {
        return new VideoCastMemberEntity(
                VideoCastMemberID.from(entity.getId(), castMemberID.getValue()),
                entity
        );
    }

}
