package com.codelabs.admin.catalago.application.ports.in;

import com.codelabs.admin.catalago.domain.category.Category;
import com.codelabs.admin.catalago.domain.pagination.SearchQuery;
import com.codelabs.admin.catalago.domain.pagination.Pagination;

public interface CategoryUseCase {

    Category create(final Category category);

    void deleteById(final String id);

    Category getById(final String id);

    Category update(final Category category);

    Pagination<Category> listCategories(final SearchQuery query);
}
