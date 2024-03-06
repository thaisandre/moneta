package com.tta.moneta.bill.presentation;

import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.tta.moneta.bill.domain.Bill;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@AllArgsConstructor
@Component
public class BillImportFileValidator implements Validator {

    private static final int MAX_BATCH_SIZE = 1000;
    private final BillCsvReadService billCsvReadService;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(DtoImportBills.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (errors.hasErrors()) return;
        DtoImportBills dtoImportBills = (DtoImportBills) target;

        addErrorIfFileEmpty(dtoImportBills, errors);
        addErrorIfInvalidFileFormat(dtoImportBills, errors);
        if(!errors.hasErrors()) addErrorsIfInvalidFileContent(dtoImportBills.file(), errors);
    }

    private void addErrorIfFileEmpty(DtoImportBills dtoImportBills, Errors errors) {
        if(dtoImportBills.hasEmptyFile()) errors.rejectValue("file", "file.empty", "File is empty");
    }

    private void addErrorIfInvalidFileFormat(DtoImportBills dtoImportBills, Errors errors) {
        if(!dtoImportBills.hasCsvFile()) errors.rejectValue("file", "file.invalid", "File must be a CSV");
    }

    private void addErrorsIfInvalidFileContent(MultipartFile file, Errors errors) {
        try {
            List<Bill> bills =  billCsvReadService.toBill(file);
            addErrorsIfInvalidFileSize(errors, bills);
        } catch (Exception e) {
            addInvalidCsvParseErrors(e, errors);
            addInvalidDataErrors(e, errors);
            if(!errors.hasErrors()) addGenericError(errors);
        }
    }

    private void addErrorsIfInvalidFileSize(Errors errors, List<Bill> bills) {
        if(bills.isEmpty()) errors.rejectValue("file", "file.invalid.size", "Csv file data cannot be empty");
        if(bills.size() > MAX_BATCH_SIZE) errors.rejectValue("file", "file.invalid.size", "Csv file cannot have more than 1000 records");
    }

    private void addInvalidCsvParseErrors(Exception e, Errors errors) {
        if(e.getCause() != null && e.getCause().getClass().isAssignableFrom(CsvRequiredFieldEmptyException.class)) {
            String defaultMessage = String.format("%s %s", e.getMessage(), e.getCause().getMessage());
            errors.rejectValue("file", "file.invalid.csv", defaultMessage);
        }
    }

    private void addInvalidDataErrors(Exception e, Errors errors) {
        if (e instanceof DtoImportRowException) {
            String defaultMessage = String.format("%s", e.getMessage());
            errors.rejectValue("file", "file.invalid.csv.data", defaultMessage);
        }
    }

    private void addGenericError(Errors errors) {
        errors.rejectValue("file", "file.error", "Error reading file. Please check the file format and try again.");
    }
}
