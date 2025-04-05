package com.codelabs.admin.catalago.common.exceptions;

public class InternalErrorException extends RuntimeException {

    public InternalErrorException(final String message) {
        super(message);
    }
}
