package com.glady.challenge.usecase.exception;

public class CompanyNotFoundException extends ResourceNotFoundException{
    public CompanyNotFoundException(Long resourceId) {
        super("Company", resourceId);
    }
}
