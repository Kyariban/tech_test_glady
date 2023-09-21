package com.glady.challenge.usecase.exception;

import lombok.Getter;

@Getter
public class SimpleMessageException extends RuntimeException {
    private final String errorDetail;

    public SimpleMessageException(String message, String errorDetail) {
        super(message);
        this.errorDetail = errorDetail;
    }
}
