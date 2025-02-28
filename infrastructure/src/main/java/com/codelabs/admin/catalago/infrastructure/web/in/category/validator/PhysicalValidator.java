package com.codelabs.admin.catalago.infrastructure.web.in.category.validator;

import br.com.fluentvalidator.AbstractValidator;
import com.codelabs.admin.catalago.domain.enums.ProblemType;
import com.codelabs.admin.catalago.infrastructure.web.in.category.dto.CategoryRequest;
import org.springframework.stereotype.Component;

import static br.com.fluentvalidator.predicate.StringPredicate.stringEmptyOrNull;
import static br.com.fluentvalidator.predicate.StringPredicate.stringSizeBetween;
import static java.util.function.Predicate.not;

@Component
public class PhysicalValidator extends AbstractValidator<CategoryRequest> {

    private static final String NAME = "name";
    public static final int NAME_MAX_LENGTH = 255;
    public static final int NAME_MIN_LENGTH = 3;

    @Override
    public void rules() {
        ruleFor(categoryRequest -> categoryRequest)

                // NAME
                .must(not(stringEmptyOrNull(CategoryRequest::name)))
                .withFieldName(NAME)
                .withCode(ProblemType.NAME_MUST_NOT_BE_NULL.name())
                .withMessage(ProblemType.NAME_MUST_NOT_BE_NULL.getDescription())

                .must(stringSizeBetween(CategoryRequest::name, NAME_MIN_LENGTH, NAME_MAX_LENGTH))
                .when(not(stringEmptyOrNull(CategoryRequest::name)))
                .withFieldName(NAME)
                .withCode(ProblemType.NAME_SIZE_MINIMUM_MAXIMUM.name())
                .withMessage(ProblemType.NAME_SIZE_MINIMUM_MAXIMUM.getDescription());

    }
}
