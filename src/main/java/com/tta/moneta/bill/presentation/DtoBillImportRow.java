package com.tta.moneta.bill.presentation;

import com.opencsv.bean.CsvBindByName;
import com.tta.moneta.bill.domain.Bill;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static java.time.LocalDate.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static org.apache.commons.lang3.StringUtils.trim;

public class DtoBillImportRow {

    @CsvBindByName(column = "expirationDate", required = true)
    private String expirationDate;

    @CsvBindByName(column = "value", required = true)
    private String value;

    @CsvBindByName(column = "description", required = true)
    private String description;

    @Deprecated
    public DtoBillImportRow() {}

    public Bill toBill() {
        validateExpirationDate();
        validateValue();
        return new Bill(LocalDate.parse(expirationDate, ofPattern("dd/MM/yyyy")), new BigDecimal(value), trim(description));
    }

    private void validateExpirationDate() {
        validateExpirationDateFormat();
        validateExpirationDateValue();
    }

    private void validateExpirationDateFormat() {
        try {
            LocalDate.parse(expirationDate, ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            throw new DtoImportRowException("Field 'expirationDate' must be in the format dd/MM/yyyy");
        }
    }
    private void validateExpirationDateValue() {
        Optional.ofNullable(expirationDate)
                .map(d -> LocalDate.parse(d, ofPattern("dd/MM/yyyy")))
                .filter(d -> d.isAfter(now()) || d.isEqual(now()))
                .orElseThrow(() -> new DtoImportRowException("Field 'expirationDate' must be equal or after today"));
    }

    private void validateValue() {
        validateValueIsNumber();
        validateValueIsGreaterThanZero();
    }

    private void validateValueIsNumber() {
        try {
            new BigDecimal(value);
        } catch (NumberFormatException e) {
            throw new DtoImportRowException("Field 'value' must be a number");
        }
    }

    private void validateValueIsGreaterThanZero() {
        Optional.ofNullable(value)
                .map(BigDecimal::new)
                .filter(v -> v.compareTo(BigDecimal.ZERO) > 0)
                .orElseThrow(() -> new DtoImportRowException("Field 'value' must be a number greater than 0.0"));
    }
}
