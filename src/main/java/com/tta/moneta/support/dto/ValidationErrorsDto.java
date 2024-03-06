package com.tta.moneta.support.dto;

import java.util.ArrayList;
import java.util.List;

public class ValidationErrorsDto {

    private final List<FieldErrorDto> fieldErrors = new ArrayList<>();
    private final List<String> globalErrors = new ArrayList<>();

    public List<FieldErrorDto> getFieldErrors() {
        return fieldErrors;
    }

    public List<String> getGlobalErrors() {
        return globalErrors;
    }

    public void addFieldError(FieldErrorDto fieldError) {
        this.fieldErrors.add(fieldError);
    }

    public void addGlobalError(String globalError) {
        this.globalErrors.add(globalError);
    }
}
