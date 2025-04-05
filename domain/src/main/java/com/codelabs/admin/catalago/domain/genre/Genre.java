package com.codelabs.admin.catalago.domain.genre;

import com.codelabs.admin.catalago.common.utils.InstantUtils;
import com.codelabs.admin.catalago.domain.AggregateRoot;
import com.codelabs.admin.catalago.domain.category.CategoryID;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@ToString
public class Genre extends AggregateRoot<GenreID> implements Cloneable{

    private String name;
    private boolean active;
    private List<CategoryID> categories;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    protected Genre(
            final GenreID id,
            final String name,
            final boolean active,
            final List<CategoryID> categories,
            final Instant createdAt,
            final Instant updatedAt,
            final Instant deletedAt
    ) {
        super(id);
        this.name = name;
        this.categories = categories;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public static Genre newGenre(final String name, final boolean isActive) {
        final var id = GenreID.unique();
        final var now = InstantUtils.now();
        final var deletedAt = isActive ? null : now;
        return new Genre(id, name, isActive, new ArrayList<>(), now, now, deletedAt);
    }

    public static Genre newGenre(final String id,final String name, final boolean isActive) {
        final var genreID = GenreID.from(id);
        final var now = InstantUtils.now();
        return new Genre(genreID, name, isActive, new ArrayList<>(), now, now, null);
    }

    public static Genre with(final Genre genre) {
        return new Genre(
                genre.id,
                genre.name,
                genre.active,
                new ArrayList<>(genre.categories),
                genre.createdAt,
                genre.updatedAt,
                genre.deletedAt
        );
    }

    public static Genre with(
            final GenreID id,
            final String name,
            final boolean isActive,
            final List<CategoryID> categories,
            final Instant createdAt,
            final Instant updatedAt,
            final Instant deletedAt
    ) {
        return new Genre(id, name, isActive, categories, createdAt, updatedAt, deletedAt);
    }

    public Genre update(final String name, final boolean isActive, final List<CategoryID> categories) {
        if (isActive) {
            activate();
        } else {
            deactivate();
        }
        this.name = name;
        this.categories = new ArrayList<>(categories != null ? categories : Collections.emptyList());
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public Genre deactivate() {
        if (getDeletedAt() == null) {
            this.deletedAt = InstantUtils.now();
        }
        this.active = false;
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public Genre activate() {
        this.deletedAt = null;
        this.active = true;
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public List<CategoryID> getCategories() {
        return Collections.unmodifiableList(categories);
    }

    public Genre addCategory(final CategoryID categoryID) {
        if (categoryID == null) {
            return this;
        }
        this.categories.add(categoryID);
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public Genre addCategories(final List<CategoryID> categories) {
        if (categories == null || categories.isEmpty()) {
            return this;
        }
        this.categories.addAll(categories);
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public Genre removeCategory(final CategoryID categoryID) {
        if (categoryID == null) {
            return this;
        }
        this.categories.remove(categoryID);
        this.updatedAt = InstantUtils.now();
        return this;
    }

    @Override
    public Genre clone() {
        try {
            return (Genre) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
