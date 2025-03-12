package com.codelabs.admin.catalago.infrastructure.persistence.video.entity;


import com.codelabs.admin.catalago.domain.enums.MediaStatus;
import com.codelabs.admin.catalago.domain.video.AudioVideoMedia;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity(name = "AudioVideoMedia")
@Table(name = "videos_video_media")
public class AudioVideoMediaEntity {

    @Id
    private String id;

    @Column(name = "checksum", nullable = false)
    private String checksum;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "encoded_path", nullable = false)
    private String encodedPath;

    @Column(name = "media_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private MediaStatus status;

    public static AudioVideoMediaEntity from(final AudioVideoMedia media) {
        return new AudioVideoMediaEntity(
                media.getId(),
                media.getChecksum(),
                media.getName(),
                media.getRawLocation(),
                media.getEncodedLocation(),
                media.getStatus()
        );
    }

    public AudioVideoMedia toDomain() {
        return AudioVideoMedia.with(
                getId(),
                getChecksum(),
                getName(),
                getFilePath(),
                getEncodedPath(),
                getStatus()
        );
    }
}
