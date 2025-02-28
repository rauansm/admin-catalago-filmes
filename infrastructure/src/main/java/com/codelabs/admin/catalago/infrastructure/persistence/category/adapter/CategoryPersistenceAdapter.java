package com.codelabs.admin.catalago.infrastructure.persistence.category.adapter;

import com.codelabs.admin.catalago.application.ports.out.CategoryPort;
import com.codelabs.admin.catalago.common.exceptions.NotFoundException;
import com.codelabs.admin.catalago.common.stereotype.PersistenceAdapter;
import com.codelabs.admin.catalago.domain.category.Category;
import com.codelabs.admin.catalago.domain.category.CategoryID;
import com.codelabs.admin.catalago.domain.category.CategorySearchQuery;
import com.codelabs.admin.catalago.domain.pagination.Pagination;
import com.codelabs.admin.catalago.infrastructure.persistence.category.entity.CategoryEntity;
import com.codelabs.admin.catalago.infrastructure.persistence.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

import static com.codelabs.admin.catalago.common.utils.SpecificationUtils.like;
import static net.logstash.logback.argument.StructuredArguments.kv;
import static net.logstash.logback.marker.Markers.append;

@PersistenceAdapter
@RequiredArgsConstructor
@Slf4j
public class CategoryPersistenceAdapter implements CategoryPort {

    private final CategoryRepository categoryRepository;

    @Override
    public Category save(Category category) {
        log.info(append("category", category), "Starting category persistence in the database...");

        CategoryEntity entity = CategoryEntity.from(category);
        log.info(append("entity", entity), "Object mapped successfully!");

        CategoryEntity savedEntity = categoryRepository.save(entity);

        log.info(append("entity", savedEntity), "Category persisted successfully!");
        return savedEntity.toAggregate();
    }

    @Override
    public Category getById(CategoryID id) {
        log.info("Searching category in the database... {}", id.getValue());

        Optional<CategoryEntity> categoryEntity = this.categoryRepository.findById(id.getValue());

        categoryEntity.ifPresent(
                entity -> log.info(append("entity", entity), "Category found successfully!"));

        Category category = categoryEntity
                .map(CategoryEntity::toAggregate)
                .orElseThrow(() -> new NotFoundException(String.format("Category not found in database with id %s", id.getValue())));

        log.info(append("category", category), "Entity to domain mapping done!");
        return category;
    }

    @Override
    public void deleteById(CategoryID id) {
        log.info("Starting category deletion in the database... {}", id.getValue());

        final String idValue = id.getValue();
        if (this.categoryRepository.existsById(idValue)) {
            this.categoryRepository.deleteById(idValue);
            log.info("Category deleted successfully! {}", idValue);
        }

    }

    @Override
    public Pagination<Category> listCategories(CategorySearchQuery query) {
        log.info(append("params", query), "Searching category in database by parameters");

        // Paginação
        final var page = PageRequest.of(
                query.page(),
                query.perPage(),
                Sort.by(Sort.Direction.fromString(query.direction()), query.sort())
        );

        // Busca dinamica pelo criterio terms (name ou description)
        final var specifications = Optional.ofNullable(query.terms())
                .filter(str -> !str.isBlank())
                .map(str -> {
                    final Specification<CategoryEntity> nameLike = like("name", str);
                    final Specification<CategoryEntity> descriptionLike = like("description", str);
                    return nameLike.or(descriptionLike);
                })
                .orElse(null);

        final var pageResult =
                this.categoryRepository.findAll(Specification.where(specifications), page);
        log.info("were found {} categories", kv("categories_size", pageResult.getTotalElements()));

        return new Pagination<>(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.map(CategoryEntity::toAggregate).toList()
        );
    }
}
