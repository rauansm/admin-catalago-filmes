package com.codelabs.admin.catalago.infrastructure.web.in.category.mapper;

import com.codelabs.admin.catalago.domain.category.Category;
import com.codelabs.admin.catalago.infrastructure.web.in.category.dto.CategoryRequest;
import org.springframework.stereotype.Component;

import java.util.function.Function;

import static com.codelabs.admin.catalago.domain.category.Category.newCategory;

@Component
public class CategoryControllerMapper {

    public Category toDomain(final CategoryRequest request) {
        return newCategory(
                request.name(),
                request.description(),
                request.active() != null ? request.active() : true);
    }

    public Category toDomain(final CategoryRequest request, final String id) {
        return newCategory(
                id,
                request.name(),
                request.description(),
                request.active() != null ? request.active() : true
        );
    }

    public <T> T toResponse(final Category category, Function<Category, T> mapper) {
        return mapper.apply(category);
    }
}
