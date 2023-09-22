package com.glady.challenge.usecase.exception;

public class MissingBodyParameterException extends SimpleMessageException{
    public MissingBodyParameterException(String message) {
        super(message, "Missing body parameter");
    }
}
