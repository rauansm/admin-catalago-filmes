package com.codelabs.admin.catalago.common.exceptions;

public class NotFoundException extends RuntimeException {

    public NotFoundException(final String aMessage) {
        super(aMessage);
    }

}
