package com.codelabs.admin.catalago.infrastructure.persistence.category;

import com.codelabs.admin.catalago.MySQLAdapterTest;
import com.codelabs.admin.catalago.domain.category.Category;
import com.codelabs.admin.catalago.infrastructure.persistence.category.entity.CategoryEntity;
import com.codelabs.admin.catalago.infrastructure.persistence.category.repository.CategoryRepository;
import org.hibernate.PropertyValueException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

@MySQLAdapterTest
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void givenAnInvalidNullName_whenCallsSave_shouldReturnError() {
        final var expectedPropertyName = "name";
        final var expectedMessage = "not-null property references a null or transient value : com.codelabs.admin.catalago.infrastructure.persistence.category.entity.CategoryEntity.name";

        final var category = Category.newCategory("Filmes", "A categoria mais assistida", true);

        final var entity = CategoryEntity.from(category);
        entity.setName(null);

        final var actualException =
                Assertions.assertThrows(DataIntegrityViolationException.class, () -> categoryRepository.save(entity));

        final var actualCause =
                Assertions.assertInstanceOf(PropertyValueException.class, actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedMessage, actualCause.getMessage());
    }

    @Test
    public void givenAnInvalidNullCreatedAt_whenCallsSave_shouldReturnError() {
        final var expectedPropertyName = "createdAt";
        final var expectedMessage = "not-null property references a null or transient value : com.codelabs.admin.catalago.infrastructure.persistence.category.entity.CategoryEntity.createdAt";

        final var category = Category.newCategory("Filmes", "A categoria mais assistida", true);

        final var entity = CategoryEntity.from(category);
        entity.setCreatedAt(null);

        final var actualException =
                Assertions.assertThrows(DataIntegrityViolationException.class, () -> categoryRepository.save(entity));

        final var actualCause =
                Assertions.assertInstanceOf(PropertyValueException.class, actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedMessage, actualCause.getMessage());
    }

    @Test
    public void givenAnInvalidNullUpdatedAt_whenCallsSave_shouldReturnError() {
        final var expectedPropertyName = "updatedAt";
        final var expectedMessage = "not-null property references a null or transient value : com.codelabs.admin.catalago.infrastructure.persistence.category.entity.CategoryEntity.updatedAt";

        final var category = Category.newCategory("Filmes", "A categoria mais assistida", true);

        final var entity = CategoryEntity.from(category);
        entity.setUpdatedAt(null);

        final var actualException =
                Assertions.assertThrows(DataIntegrityViolationException.class, () -> categoryRepository.save(entity));

        final var actualCause =
                Assertions.assertInstanceOf(PropertyValueException.class, actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedMessage, actualCause.getMessage());
    }
}
