package com.glady.challenge.usecase.delegate.error;

import com.glady.challenge.usecase.openapi.model.ErrorDetail;
import com.glady.challenge.usecase.openapi.model.GenericError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
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
                // missing parameter

                addErrorDetail(genericError, SocErrorEnum.SOC002, error.getObjectName());
            } else {
                // invalid parameter
                addErrorDetail(genericError, SocErrorEnum.SOC003, error.getObjectName());
            }
        }
        for (FieldError fieldError : fieldErrors) {
            if ("NotNull".equals(fieldError.getCode())) {
                // missing parameter
                addErrorDetail(genericError, SocErrorEnum.SOC002, fieldError.getField());
            } else {
                // invalid parameter
                addErrorDetail(genericError, SocErrorEnum.SOC003, fieldError.getField());
            }
        }
        return genericError;
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<GenericError> processRuntimeException(Exception ex) {
        ResponseEntity.BodyBuilder builder;
        GenericError genericError = new GenericError();

        if (ex instanceof HttpMessageConversionException) {
            builder = ResponseEntity.status(HttpStatus.BAD_REQUEST);
            processHttpMessageConvertionException((HttpMessageConversionException) ex, erreurGenerique);
        } else if (ex instanceof MethodArgumentTypeMismatchException) {
            builder = ResponseEntity.status(HttpStatus.BAD_REQUEST);
            addErrorDetail(erreurGenerique, SocErrorEnum.SOC003, ((MethodArgumentTypeMismatchException) ex).getName());
        } else if (ex instanceof MissingServletRequestParameterException) {
            builder = ResponseEntity.status(HttpStatus.BAD_REQUEST);
            addErrorDetail(erreurGenerique, SocErrorEnum.SOC002, ((MissingServletRequestParameterException) ex).getParameterName());
        } else if (ex instanceof HttpRequestMethodNotSupportedException) {
            builder = ResponseEntity.status(HttpStatus.BAD_REQUEST);
            addErrorDetail(erreurGenerique, SocErrorEnum.SOC002, ((HttpRequestMethodNotSupportedException) ex).getMethod());
        } else if (ex instanceof ConstraintViolationException) {
            builder = ResponseEntity.status(HttpStatus.BAD_REQUEST);
            processConstraintViolationException((ConstraintViolationException) ex, erreurGenerique);
        } else if (ex instanceof DataIntegrityViolationException) {
            builder = ResponseEntity.status(HttpStatus.BAD_REQUEST);
            addErrorDetail(erreurGenerique, SocErrorEnum.SOC003, ((DataIntegrityViolationException) ex).getMostSpecificCause().getMessage());
        } else if (ex instanceof InsufficientAuthenticationException) {
            log.error("Authorization error");
            builder = ResponseEntity.status(HttpStatus.UNAUTHORIZED);
            addErrorDetail(erreurGenerique, SocErrorEnum.SOC006, "authorization header");
        } else {
            log.error("Erreur interne", ex); // besoin de la loguer
            builder = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
            addErrorDetail(erreurGenerique, SocErrorEnum.SOC001);
        }
        return builder.body(genericError);
    }


}
