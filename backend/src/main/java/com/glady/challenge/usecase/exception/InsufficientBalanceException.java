package com.glady.challenge.usecase.exception;

public class InsufficientBalanceException extends SimpleMessageException {
    public InsufficientBalanceException(String message){
        super(message, "Insufficient Balance Exception");
    }
}
