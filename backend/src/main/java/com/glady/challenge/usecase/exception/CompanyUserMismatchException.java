package com.glady.challenge.usecase.exception;

public class CompanyUserMismatchException extends SimpleMessageException {
    public CompanyUserMismatchException(String message) {
        super(message, "User does not belong to the company");
    }
}
