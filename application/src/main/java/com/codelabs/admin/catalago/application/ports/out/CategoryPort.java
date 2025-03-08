package com.codelabs.admin.catalago.application.ports.out;

import com.codelabs.admin.catalago.domain.category.Category;
import com.codelabs.admin.catalago.domain.category.CategoryID;
import com.codelabs.admin.catalago.domain.pagination.Pagination;
import com.codelabs.admin.catalago.domain.pagination.SearchQuery;

import java.util.List;

public interface CategoryPort {
    Category save(final Category category);

    Category getById(final CategoryID id);

    void deleteById(final CategoryID id);

    Pagination<Category> listCategories(final SearchQuery query);

    List<CategoryID> existsByIds(final Iterable<CategoryID> ids);
}
