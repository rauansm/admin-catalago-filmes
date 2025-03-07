package com.codelabs.admin.catalago.infrastructure.web.in.castmember.validator;

import br.com.fluentvalidator.AbstractValidator;
import com.codelabs.admin.catalago.domain.enums.ProblemType;
import com.codelabs.admin.catalago.infrastructure.web.in.castmember.dto.CastMemberRequest;
import org.springframework.stereotype.Component;

import static br.com.fluentvalidator.predicate.StringPredicate.stringEmptyOrNull;
import static br.com.fluentvalidator.predicate.StringPredicate.stringSizeBetween;
import static java.util.function.Predicate.not;

@Component
public class CastMemberValidator extends AbstractValidator<CastMemberRequest> {

    private static final String NAME = "name";
    private static final String TYPE = "type";
    public static final int NAME_MAX_LENGTH = 255;
    public static final int NAME_MIN_LENGTH = 3;

    @Override
    public void rules() {
        ruleFor(castMemberRequest -> castMemberRequest)
                // NAME
                .must(not(stringEmptyOrNull(CastMemberRequest::name)))
                .withFieldName(NAME)
                .withCode(ProblemType.NAME_MUST_NOT_BE_NULL.name())
                .withMessage(ProblemType.NAME_MUST_NOT_BE_NULL.getDescription())

                .must(stringSizeBetween(CastMemberRequest::name, NAME_MIN_LENGTH, NAME_MAX_LENGTH))
                .when(not(stringEmptyOrNull(CastMemberRequest::name)))
                .withFieldName(NAME)
                .withCode(ProblemType.NAME_SIZE_MINIMUM_MAXIMUM.name())
                .withMessage(ProblemType.NAME_SIZE_MINIMUM_MAXIMUM.getDescription());

        ruleFor(castMemberRequest -> castMemberRequest)
                // TYPE
                .must(request -> request.type() != null)
                .withFieldName(TYPE)
                .withCode(ProblemType.TYPE_MUST_NOT_BE_NULL.name())
                .withMessage(ProblemType.TYPE_MUST_NOT_BE_NULL.getDescription());
    }
}
