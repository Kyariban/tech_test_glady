package com.glady.challenge.usecase.delegate.error;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.glady.challenge.usecase.exception.ResourceNotFoundException;
import com.glady.challenge.usecase.exception.SimpleMessageException;
import com.glady.challenge.usecase.openapi.model.ErrorDetail;
import com.glady.challenge.usecase.openapi.model.GenericError;
import com.google.common.base.Throwables;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@Order
@ControllerAdvice
@RequiredArgsConstructor
public class ErrorHandler {

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<GenericError> processRuntimeException(Exception ex) {
        ResponseEntity.BodyBuilder builder;
        GenericError genericError = new GenericError();

        if (ex instanceof ResourceNotFoundException resourceNotFoundException) {
            builder = ResponseEntity.status(HttpStatus.NOT_FOUND);
            handleResourceNotFoundException(resourceNotFoundException, genericError);
        } else if (ex instanceof SimpleMessageException simpleMessageException) {
            builder = ResponseEntity.status(HttpStatus.BAD_REQUEST);
            addErrorDetail(genericError, simpleMessageException.getErrorDetail(), simpleMessageException.getMessage());
        } else if (ex instanceof MethodArgumentTypeMismatchException mismatchException) {
            builder = ResponseEntity.status(HttpStatus.BAD_REQUEST);
            handleTypeMismatchException(mismatchException, genericError);
        } else if (ex instanceof MissingPathVariableException pathVariableException) {
            builder = ResponseEntity.status(HttpStatus.BAD_REQUEST);
            handleMissingPathParamException(pathVariableException, genericError);
        } else if (ex instanceof HttpMessageNotReadableException) {
            builder = handleMessageNotReadableException(ex, genericError);
        } else {
            builder = handleInternalServerError(ex, genericError);
        }
        return builder.body(genericError);
    }

    private void handleResourceNotFoundException(ResourceNotFoundException ex, GenericError genericError) {
        Long resourceId = ex.getResourceId();
        String resourceName = ex.getResourceName();

        addErrorDetail(
                genericError,
                "Resource not found",
                String.format("%s with id : %d not found", resourceName, resourceId)
        );
    }

    private void handleTypeMismatchException(MethodArgumentTypeMismatchException mismatchException, GenericError genericError) {
        addErrorDetail(
                genericError,
                "Parameter Type Mismatch",
                String.format(
                        "Wrong value for parameter %s, expected type is %s",
                        mismatchException.getPropertyName(),
                        mismatchException.getParameter().getParameterType().getSimpleName()
                )
        );
    }

    private void handleMissingPathParamException(MissingPathVariableException pathVariableException, GenericError genericError) {
        addErrorDetail(genericError,
                "Missing path parameter",
                String.format("Missing required parameter %s", pathVariableException.getVariableName())
        );
    }

    private ResponseEntity.BodyBuilder handleMessageNotReadableException(Exception ex, GenericError genericError) {
        ResponseEntity.BodyBuilder builder;
        builder = ResponseEntity.status(HttpStatus.BAD_REQUEST);
        Throwable rootCause = Throwables.getRootCause(ex);
        if(rootCause instanceof MismatchedInputException mismatchedInputException) {
            handleWrongFormatParameter(mismatchedInputException, genericError);
        } else if (rootCause instanceof IllegalArgumentException illegalArgumentException) {
            handleIllegalArgument(illegalArgumentException, genericError);
        } else {
            builder = handleInternalServerError(ex, genericError);
        }
        return builder;
    }

    private void handleIllegalArgument(IllegalArgumentException illegalArgumentException, GenericError genericError) {
        addErrorDetail(
                genericError,
                "Illegal Argument",
                illegalArgumentException.getMessage()
        );
    }

    private void handleWrongFormatParameter(MismatchedInputException mismatchedInputException, GenericError genericError) {
        addErrorDetail(
                genericError,
                "Invalid Parameter Format",
                String.format(
                        "Wrong format for parameter %s, expected %s",
                        mismatchedInputException.getPath().get(0).getFieldName(),
                        mismatchedInputException.getTargetType().getSimpleName()
                )
        );
    }

    private ResponseEntity.BodyBuilder handleInternalServerError(Exception ex, GenericError genericError) {
        ResponseEntity.BodyBuilder builder;
        log.error("Internal Server Error", ex);
        builder = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
        addErrorDetail(genericError, "Internal Server Error", ex.getMessage());
        return builder;
    }

    private void addErrorDetail(GenericError genericError, String detail, String message) {
        ErrorDetail errorDetail = new ErrorDetail()
                .detail(detail)
                .message(message);
        genericError.addErrorsItem(errorDetail);
    }
}
