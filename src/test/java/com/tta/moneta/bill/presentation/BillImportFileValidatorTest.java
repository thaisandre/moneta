package com.tta.moneta.bill.presentation;

import com.tta.moneta.bill.domain.Bill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BillImportFileValidatorTest {

    private BillImportFileValidator billImportFileValidator;
    private BillCsvReadService billCsvReadService;

    @BeforeEach
    void setUp() {
        billCsvReadService = mock(BillCsvReadService.class);
        billImportFileValidator = new BillImportFileValidator(billCsvReadService);
    }

    @Test
    void supports__shouldReturnTrueWhenClazzIsInstanceOfDtoImportBills() {
        assertThat(billImportFileValidator.supports(DtoImportBills.class)).isTrue();
    }

    @Test
    void supports__shouldReturnFalseWhenClazzIsNotInstanceOfDtoImportBills() {
        assertThat(billImportFileValidator.supports(String.class)).isFalse();
    }

    @Test
    void validate__shouldAddErrorsWhenFileIsNull() {
        DtoImportBills target = new DtoImportBills(null);
        BindingResult result = new BeanPropertyBindingResult(target, "dtoImportBills");

        billImportFileValidator.validate(target, result);
        assertThat(result.getFieldErrors()).hasSize(2)
                .extracting(FieldError::getField, FieldError::getDefaultMessage)
                .containsExactly(tuple("file", "File is empty"),
                        tuple("file", "File must be a CSV"));
    }

    @Test
    void validate__shouldAddErrorWhenFileIsEmpty() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);
        when(file.getContentType()).thenReturn("text/csv");

        DtoImportBills target = new DtoImportBills(file);
        BindingResult result = new BeanPropertyBindingResult(target, "dtoImportBills");

        billImportFileValidator.validate(target, result);
        assertThat(result.getFieldErrors()).hasSize(1)
                .extracting(FieldError::getField, FieldError::getDefaultMessage)
                .containsExactly(tuple("file", "File is empty"));
    }

    @Test
    void validate__shouldAddErrorWhenFileIsNotCsvFile() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("text/plain");

        DtoImportBills target = new DtoImportBills(file);
        BindingResult result = new BeanPropertyBindingResult(target, "dtoImportBills");

        billImportFileValidator.validate(target, result);
        assertThat(result.getFieldErrors()).hasSize(1)
                .extracting(FieldError::getField, FieldError::getDefaultMessage)
                .containsExactly(tuple("file", "File must be a CSV"));
    }

    @Test
    void validate__shouldAddErrorsWhenFileHasNoData() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("text/csv");

        when(billCsvReadService.toBill(file)).thenReturn(emptyList());

        DtoImportBills target = new DtoImportBills(file);
        BindingResult result = new BeanPropertyBindingResult(target, "dtoImportBills");

        billImportFileValidator.validate(target, result);
        assertThat(result.getFieldErrors()).extracting(FieldError::getField, FieldError::getDefaultMessage)
                .containsExactly(tuple("file", "Csv file data cannot be empty"));
    }


    @Test
    void validate__shouldAddErrorsWhenFileHasMoreThan1000Records() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("text/csv");

        List<Bill> billList = new ArrayList<>();
        IntStream.range(0, 1001).forEach(i -> billList.add(mock(Bill.class)));
        when(billCsvReadService.toBill(file)).thenReturn(billList);

        DtoImportBills target = new DtoImportBills(file);
        BindingResult result = new BeanPropertyBindingResult(target, "dtoImportBills");

        billImportFileValidator.validate(target, result);
        assertThat(result.getFieldErrors()).extracting(FieldError::getField, FieldError::getDefaultMessage)
                .containsExactly(tuple("file", "Csv file cannot have more than 1000 records"));
    }

    @Test
    void validate__shouldAddErrorsWithSpecificMessageWhenThrowCsvRequiredFieldEmptyException() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("text/csv");

        when(billCsvReadService.toBill(file)).thenThrow(new RuntimeException());

        DtoImportBills target = new DtoImportBills(file);
        BindingResult result = new BeanPropertyBindingResult(target, "dtoImportBills");

        billImportFileValidator.validate(target, result);
        assertThat(result.getFieldErrors()).extracting(FieldError::getField, FieldError::getDefaultMessage)
                .containsExactly(tuple("file","Error reading file. Please check the file format and try again."));
    }

    @Test
    void validate__shouldAddErrorsWithDtoImportRowExceptionMessageWhenThrowDtoBillImportRowException() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("text/csv");
        when(billCsvReadService.toBill(file)).thenThrow(new DtoImportRowException("Data invalid exception"));

        DtoImportBills target = new DtoImportBills(file);
        BindingResult result = new BeanPropertyBindingResult(target, "dtoImportBills");

        billImportFileValidator.validate(target, result);
        assertThat(result.getFieldErrors()).extracting(FieldError::getField, FieldError::getDefaultMessage)
                .containsExactly(tuple("file", "Data invalid exception"));
    }

    @Test
    void validate__shouldAddErrorsWithGenericMessageWhenThrowOtherExceptionThatIsNotCsvRequiredFieldEmptyExceptionOrDtoBillImportRowException() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("text/csv");
        when(billCsvReadService.toBill(file)).thenThrow(new RuntimeException());

        DtoImportBills target = new DtoImportBills(file);
        BindingResult result = new BeanPropertyBindingResult(target, "dtoImportBills");

        billImportFileValidator.validate(target, result);
        assertThat(result.getFieldErrors()).extracting(FieldError::getField, FieldError::getDefaultMessage)
                .containsExactly(tuple("file", "Error reading file. Please check the file format and try again."));
    }

    @Test
    void validate__shouldNotAddErrorsWhenFileAndFileDataIsValid() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("text/csv");
        when(billCsvReadService.toBill(file)).thenReturn(List.of(mock(Bill.class), mock(Bill.class)));

        DtoImportBills target = new DtoImportBills(file);
        BindingResult result = new BeanPropertyBindingResult(target, "dtoImportBills");

        billImportFileValidator.validate(target, result);
        assertThat(result.getFieldErrors()).isEmpty();
    }
}