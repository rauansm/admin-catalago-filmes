package com.codelabs.admin.catalago.common.exceptions;

import br.com.fluentvalidator.context.ValidationResult;
import br.com.fluentvalidator.exception.ValidationException;

public class PhysicalValidationException extends ValidationException {

    private static final long serialVersionUID = 7392211289515836252L;

    public PhysicalValidationException(final ValidationResult validationResult) {
        super(validationResult);
    }

}
