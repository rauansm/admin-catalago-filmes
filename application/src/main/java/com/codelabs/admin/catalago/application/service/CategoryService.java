package com.codelabs.admin.catalago.application.service;

import com.codelabs.admin.catalago.application.ports.in.CategoryUseCase;
import com.codelabs.admin.catalago.application.ports.out.CategoryPort;
import com.codelabs.admin.catalago.common.stereotype.UseCase;
import com.codelabs.admin.catalago.domain.category.Category;
import com.codelabs.admin.catalago.domain.category.CategoryID;
import com.codelabs.admin.catalago.domain.pagination.Pagination;
import com.codelabs.admin.catalago.domain.pagination.SearchQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static net.logstash.logback.marker.Markers.append;

@UseCase
@RequiredArgsConstructor
@Slf4j
public class CategoryService implements CategoryUseCase {

    public static final String CATEGORY_FIELD = "category";
    private final CategoryPort categoryPort;

    @Override
    public Category create(final Category category) {
        log.info(append(CATEGORY_FIELD, category), "Starting category creation service");

        final var savedCategory = this.categoryPort.save(category);

        log.info("Category creation service completed successfully.");

        return savedCategory;
    }

    @Override
    public void deleteById(final String id) {
        log.info(append("id", id), "delete category by id");

        this.categoryPort.deleteById(CategoryID.from(id));

        log.info("Category delete service completed successfully.");
    }

    @Override
    public Category getById(final String id) {
        log.info(append("id", id), "Search category by id");

        final var category = this.categoryPort.getById(CategoryID.from(id));
        log.info(append(CATEGORY_FIELD, category), "Category successfully found.");

        return category;
    }

    @Override
    public Category update(final Category category) {
        log.info(append(CATEGORY_FIELD, category), "Starting category update service");

        final var categoryFound = this.categoryPort.getById(category.getId());
        categoryFound.update(category.getName(), category.getDescription(), category.isActive());
        log.info(append(CATEGORY_FIELD, categoryFound), "Category update successfully.");

        return this.categoryPort.save(categoryFound);
    }

    @Override
    public Pagination<Category> listCategories(final SearchQuery query) {
        log.info(append("query", query), "Search all category by query params");

        final var categories = this.categoryPort.listCategories(query);
        log.info(append("categories", categories), "Found categories");

        return categories;
    }
}
