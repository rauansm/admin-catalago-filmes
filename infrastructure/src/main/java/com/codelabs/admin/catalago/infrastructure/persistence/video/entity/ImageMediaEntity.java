package com.codelabs.admin.catalago.infrastructure.persistence.video.entity;

import com.codelabs.admin.catalago.domain.video.ImageMedia;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity(name = "ImageMedia")
@Table(name = "videos_image_media")
public class ImageMediaEntity {

    @Id
    private String id;

    @Column(name = "checksum", nullable = false)
    private String checksum;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    public static ImageMediaEntity from(final ImageMedia media) {
        return new ImageMediaEntity(
                media.getId(),
                media.getChecksum(),
                media.getName(),
                media.getLocation()
        );
    }

    public ImageMedia toDomain() {
        return ImageMedia.with(
                getId(),
                getChecksum(),
                getName(),
                getFilePath()
        );
    }
}
