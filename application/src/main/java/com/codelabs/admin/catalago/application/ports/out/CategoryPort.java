package com.codelabs.admin.catalago.application.ports.out;

import com.codelabs.admin.catalago.domain.category.Category;
import com.codelabs.admin.catalago.domain.category.CategoryID;
import com.codelabs.admin.catalago.domain.category.CategorySearchQuery;
import com.codelabs.admin.catalago.domain.pagination.Pagination;

public interface CategoryPort {
    Category save(final Category category);

    Category getById(final CategoryID id);

    void deleteById(final CategoryID id);

    Pagination<Category> listCategories(final CategorySearchQuery query);
}
