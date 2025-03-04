package com.codelabs.admin.catalago.infrastructure.persistence.genre.entity;

import com.codelabs.admin.catalago.domain.category.CategoryID;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "genres_categories")
public class GenreCategoryEntity {

    @EmbeddedId
    private GenreCategoryID id;

    @ManyToOne
    @MapsId("genreId")
    private GenreEntity genre;

    private GenreCategoryEntity(final GenreEntity genre, final CategoryID categoryID) {
        this.id = GenreCategoryID.from(genre.getId(), categoryID.getValue());
        this.genre = genre;
    }

    public static GenreCategoryEntity from(final GenreEntity genre, final CategoryID categoryID) {
        return new GenreCategoryEntity(genre, categoryID);
    }
}
