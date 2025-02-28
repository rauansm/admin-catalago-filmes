package com.codelabs.admin.catalago.domain.enums;

import lombok.Getter;

@Getter
public enum ProblemType {

    NAME_MUST_NOT_BE_NULL("Name must be provided", "NAME_MUST_NOT_BE_NULL"),
    NAME_SIZE_MINIMUM_MAXIMUM("'name' must be between 3 and 255 characters", "NAME_SIZE_MINIMUM_MAXIMUM"),
    INVALID_PARAMETER("Invalid parameter", "INVALID_PARAMETER");

    private final String description;
    private final String code;

    ProblemType(String description, String code) {
        this.description = description;
        this.code = code;
    }

}
