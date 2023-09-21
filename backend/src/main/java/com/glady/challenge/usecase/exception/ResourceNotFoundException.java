package com.glady.challenge.usecase.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {

    private final String resourceName;
    private final Long resourceId;

    public ResourceNotFoundException(String resourceName, Long resourceId) {
        this.resourceName = resourceName;
        this.resourceId = resourceId;
    }
}
