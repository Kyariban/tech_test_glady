package com.glady.challenge.usecase.exception;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(Long resourceId) {
        super("User", resourceId);
    }
}
