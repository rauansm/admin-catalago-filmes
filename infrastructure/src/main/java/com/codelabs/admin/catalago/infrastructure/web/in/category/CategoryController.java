package com.codelabs.admin.catalago.infrastructure.web.in.category;

import com.codelabs.admin.catalago.application.ports.in.CategoryUseCase;
import com.codelabs.admin.catalago.common.exceptions.PhysicalValidationException;
import com.codelabs.admin.catalago.domain.category.Category;
import com.codelabs.admin.catalago.domain.category.CategorySearchQuery;
import com.codelabs.admin.catalago.domain.pagination.Pagination;
import com.codelabs.admin.catalago.infrastructure.web.in.category.dto.CategoryDetailsResponse;
import com.codelabs.admin.catalago.infrastructure.web.in.category.dto.CategoryListResponse;
import com.codelabs.admin.catalago.infrastructure.web.in.category.dto.CategoryRequest;
import com.codelabs.admin.catalago.infrastructure.web.in.category.dto.CategoryResponse;
import com.codelabs.admin.catalago.infrastructure.web.in.category.mapper.CategoryControllerMapper;
import com.codelabs.admin.catalago.infrastructure.web.in.category.validator.PhysicalValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static net.logstash.logback.marker.Markers.append;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CategoryController implements CategoryAPI {

    private static final String CATEGORY_FIELD = "category";
    public static final String CATEGORY_WAS_MAPPED_SUCCESSFULLY = "Category was mapped successfully";
    private final CategoryUseCase categoryUseCase;
    private final CategoryControllerMapper mapper;
    private final PhysicalValidator validator;

    @Override
    public ResponseEntity<CategoryResponse> create(final CategoryRequest request) {
        log.info(append("body", request), "Receiving category creation request");

        validator.validate(request).isInvalidThrow(PhysicalValidationException.class);

        final Category category = mapper.toDomain(request);
        log.info(append(CATEGORY_FIELD, category), CATEGORY_WAS_MAPPED_SUCCESSFULLY);

        final Category createdCategory = categoryUseCase.create(category);
        log.info(append(CATEGORY_FIELD, category), "Category was created successfully");

        final CategoryResponse categoryResponse = mapper.toResponse(createdCategory, CategoryResponse::from);
        log.info(append(CATEGORY_FIELD, categoryResponse), "Created category was mapped successfully");

        return ResponseEntity.created(URI.create("/categories/" + categoryResponse.id()))
                .body(categoryResponse);
    }

    @Override
    public ResponseEntity<CategoryResponse> updateById(final String id, final CategoryRequest request) {
        log.info(append("id", id).and(append("body", request)), "Receiving category update request");

        validator.validate(request).isInvalidThrow(PhysicalValidationException.class);

        final Category category = mapper.toDomain(request, id);
        log.info(append(CATEGORY_FIELD, category), CATEGORY_WAS_MAPPED_SUCCESSFULLY);

        final Category updateCategory = categoryUseCase.update(category);
        log.info(append(CATEGORY_FIELD, category), "Category was update successfully");

        final CategoryResponse categoryResponse = mapper.toResponse(updateCategory, CategoryResponse::from);
        log.info(append(CATEGORY_FIELD, categoryResponse), "Updated category was mapped successfully");

        return ResponseEntity.ok(categoryResponse);
    }

    @Override
    public ResponseEntity<CategoryDetailsResponse> getById(final String id) {
        log.info("Receiving category search request {}", id);

        final Category category = categoryUseCase.getById(id);
        log.info(append(CATEGORY_FIELD, category), "Category found");

        final CategoryDetailsResponse detailsResponse = mapper.toResponse(category, CategoryDetailsResponse::from);
        log.info(append(CATEGORY_FIELD, detailsResponse), CATEGORY_WAS_MAPPED_SUCCESSFULLY);

        return ResponseEntity.ok(detailsResponse);
    }

    @Override
    public Pagination<CategoryListResponse> listCategories(final String search, final int page, final int perPage,
                                                           final String sort, final String direction) {
        log.info(append("search", search)
                        .and(append("page", page)
                                .and(append("perPage", perPage)
                                        .and(append("sort", sort)
                                                .and(append("dir", direction))))),
                "Receiving request to list categories by parameters");

        final Pagination<Category> categoryPagination = categoryUseCase.listCategories(new CategorySearchQuery(page, perPage, search, sort, direction));
        log.info(append(CATEGORY_FIELD, categoryPagination), "categories found");

        final Pagination<CategoryListResponse> listResponse = categoryPagination.map(CategoryListResponse::from);
        log.info(append(CATEGORY_FIELD, listResponse), "categories successfully mapped");

        return listResponse;
    }

    @Override
    public void deleteById(final String id) {
        log.info("Receiving category deletion request {}", id);

        categoryUseCase.deleteById(id);

        log.info("deleted category successfully");
    }
}
