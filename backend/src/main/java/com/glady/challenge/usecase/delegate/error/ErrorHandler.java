package com.glady.challenge.usecase.delegate.error;

import com.glady.challenge.usecase.exception.InsufficientBalanceException;
import com.glady.challenge.usecase.exception.ResourceNotFoundException;
import com.glady.challenge.usecase.openapi.model.ErrorDetail;
import com.glady.challenge.usecase.openapi.model.GenericError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Slf4j
@Order
@ControllerAdvice
@RequiredArgsConstructor
public class ErrorHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public GenericError methodArgumentNotValidException(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        GenericError genericError = new GenericError();

        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            if ("NotNull".equals(error.getCode())) {
                addErrorDetail(genericError, "Missing parameter", error.getObjectName());
            } else {
                addErrorDetail(genericError, "Invalid parameter", error.getObjectName());
            }
        }
        for (FieldError fieldError : fieldErrors) {
            if ("NotNull".equals(fieldError.getCode())) {
                addErrorDetail(genericError, "Missing parameter", fieldError.getField());
            } else {
                addErrorDetail(genericError, "Invalid parameter", fieldError.getField());
            }
        }
        return genericError;
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<GenericError> processRuntimeException(Exception ex) {
        ResponseEntity.BodyBuilder builder;
        GenericError genericError = new GenericError();

        if (ex instanceof ResourceNotFoundException resourceNotFoundException) {
            builder = ResponseEntity.status(HttpStatus.NOT_FOUND);
            handleResourceNotFoundException(resourceNotFoundException, genericError);
        } else if (ex instanceof InsufficientBalanceException) {
            builder = ResponseEntity.status(HttpStatus.BAD_REQUEST);
            addErrorDetail(genericError, "Insufficient Balance Exception", ex.getMessage());
        } else {
            log.error("Internal Server Error", ex);
            builder = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
            addErrorDetail(genericError, "Internal Server Error", ex.getMessage());
        }
        return builder.body(genericError);
    }

    private void handleResourceNotFoundException(ResourceNotFoundException ex, GenericError genericError) {
        Long resourceId = ex.getResourceId();
        String resourceName = ex.getResourceName();

        addErrorDetail(
                genericError,
                "Resource not found",
                resourceName + " with id : " + resourceId + " not found"
        );
    }

    private void addErrorDetail(GenericError genericError, String detail, String message) {
        ErrorDetail errorDetail = new ErrorDetail()
                .detail(detail)
                .message(message);
        genericError.addErrorsItem(errorDetail);
    }
}
