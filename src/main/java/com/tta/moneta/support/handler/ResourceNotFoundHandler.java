package com.tta.moneta.support.handler;

import com.tta.moneta.support.dto.ResourceNotFoundDto;
import com.tta.moneta.support.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ResourceNotFoundHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResourceNotFoundDto handler(ResourceNotFoundException exception) {
        return new ResourceNotFoundDto(exception.getMessage());
    }
}
