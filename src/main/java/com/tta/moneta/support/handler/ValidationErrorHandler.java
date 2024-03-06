package com.tta.moneta.support.handler;

import com.tta.moneta.support.dto.FieldErrorDto;
import com.tta.moneta.support.dto.ValidationErrorsDto;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class ValidationErrorHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ValidationErrorsDto handler(MethodArgumentNotValidException exception) {

        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        List<ObjectError> globalErrors = exception.getBindingResult().getGlobalErrors();

        ValidationErrorsDto validationErrorsDto = new ValidationErrorsDto();
        fieldErrors.stream().map(error -> new FieldErrorDto(error.getField(), error.getDefaultMessage()))
                .forEach(validationErrorsDto::addFieldError);
        globalErrors.stream().map(ObjectError::getDefaultMessage)
                .forEach(validationErrorsDto::addGlobalError);

        return validationErrorsDto;
    }
}
