package com.codelabs.admin.catalago.infrastructure.web.in.category.dto;

import com.codelabs.admin.catalago.domain.category.Category;

public record CategoryResponse(
        String id
) {
    public static CategoryResponse from(final String id) {
        return new CategoryResponse(id);
    }

    public static CategoryResponse from(final Category category) {
        return new CategoryResponse(category.getId().getValue());
    }
}
