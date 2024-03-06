package com.tta.moneta.bill.presentation;

import com.tta.moneta.bill.domain.Bill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static java.math.RoundingMode.HALF_UP;
import static java.time.LocalDate.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BillCsvReadServiceTest {

    private BillCsvReadService billCsvReadService;

    @BeforeEach
    void setUp() {
        billCsvReadService = new BillCsvReadService();
    }

    @Test
    void read__shouldThrowExceptionWhenFileIsNull() {
        assertThatThrownBy(() -> billCsvReadService.toBill(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("file is required");
    }

    @Test
    void read__shouldThrowExceptionWhenFileIsEmpty() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);
        assertThatThrownBy(() -> billCsvReadService.toBill(file))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("file cannot be empty");
    }

    @Test
    void read__shouldThrowExceptionWhenFileIsNotCsv() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("text/plain");
        assertThatThrownBy(() -> billCsvReadService.toBill(file))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("file must be a csv");
    }

    @Test
    void read__shouldThrowExceptionWhenFileHeaderIsNotInTheExpectedFormat() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("text/csv");

        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("".getBytes()));
        assertThatThrownBy(() -> billCsvReadService.toBill(file))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error capturing CSV header!");

        String csvContent = "content,value,description";
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(csvContent.getBytes()));
        assertThatThrownBy(() -> billCsvReadService.toBill(file))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error capturing CSV header!");
    }

    @Test
    void read__shouldReturnEmptyListWhenFileHeaderHasTheExpectedFormatAndFileContentDataIsEmpty() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("text/csv");

        String csvContent = "expirationDate,value,description";
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(csvContent.getBytes()));

        List<Bill> bills = billCsvReadService.toBill(file);
        assertThat(bills).isEmpty();
    }

    @Test
    void read__shouldReturnBillsListWhenCsvHeaderAndContentDataAreInTheExpectedFormat() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("text/csv");

        String expirationDate = now().format(ofPattern("dd/MM/yyyy"));
        String csvContent = """
                expirationDate,value,description
                %s,100,Conta de luz
                %s,90.5,Conta de telefone
                """.formatted(expirationDate, expirationDate);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(csvContent.getBytes()));

        List<Bill> bills = billCsvReadService.toBill(file);
        assertThat(bills)
                .hasSize(2)
                .extracting(Bill::getExpirationDate, Bill::getValue, Bill::getDescription)
                .contains(tuple(now(), BigDecimal.valueOf(100).setScale(2, HALF_UP), "Conta de luz"),
                        tuple(LocalDate.now(), BigDecimal.valueOf(90.5).setScale(2, HALF_UP), "Conta de telefone"));
    }

    @Test
    void read__shouldThrowExceptionWhenFileHeaderHasTheExpectedFormatAndFileContentDataExpirationDateIsNull() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("text/csv");

        String csvContent = """
                expirationDate,value,description
                ,100,Conta de luz
                """;
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(csvContent.getBytes()));

        assertThatThrownBy(() -> billCsvReadService.toBill(file))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error parsing CSV line: 2. [,100,Conta de luz]");
    }

    @Test
    void read__shouldThrowExceptionWhenFileHeaderHasTheExpectedFormatAndFileContentDataExpirationDateIsNotInExpectedFormat() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("text/csv");

        String csvContent = """
                expirationDate,value,description
                20-10-2023,100,Conta de luz
                """;
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(csvContent.getBytes()));

        assertThatThrownBy(() -> billCsvReadService.toBill(file))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Field 'expirationDate' must be in the format dd/MM/yyyy");
    }

    @Test
    void read__shouldThrowExceptionWhenFileContentDataExpirationDateIsNotValid() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("text/csv");

        String csvContent = """
                expirationDate,value,description
                10/02/2023,100,Conta de luz
                """;
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(csvContent.getBytes()));

        assertThatThrownBy(() -> billCsvReadService.toBill(file))
                .isInstanceOf(DtoImportRowException.class)
                .hasMessageContaining("Field 'expirationDate' must be equal or after today");
    }

    @Test
    void read__shouldThrowExceptionWhenFileContentDataValueIsEmpty() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("text/csv");

        String expirationDate = now().format(ofPattern("dd/MM/yyyy"));
        String csvContent = """
                expirationDate,value,description
                %s,,Conta de luz
                """.formatted(expirationDate);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(csvContent.getBytes()));

        assertThatThrownBy(() -> billCsvReadService.toBill(file))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error parsing CSV line: 2. [%s,,Conta de luz]".formatted(expirationDate));
    }

    @Test
    void read__shouldThrowExceptionWhenFileContentDataValueIsInIncorrectFormat() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("text/csv");

        String expirationDate = now().format(ofPattern("dd/MM/yyyy"));
        String csvContent = """
                expirationDate,value,description
                %s,content,Conta de luz
                """.formatted(expirationDate);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(csvContent.getBytes()));

        assertThatThrownBy(() -> billCsvReadService.toBill(file))
                .isInstanceOf(DtoImportRowException.class)
                .hasMessageContaining("Field 'value' must be a number");

    }

    @Test
    void read__shouldThrowExceptionWhenFileContentDataValueIsEqualOrLessThanZero() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("text/csv");

        String expirationDate = now().format(ofPattern("dd/MM/yyyy"));
        String csvContent = """
                expirationDate,value,description
                %s,0,Conta de luz
                """.formatted(expirationDate);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(csvContent.getBytes()));

        assertThatThrownBy(() -> billCsvReadService.toBill(file))
                .isInstanceOf(DtoImportRowException.class)
                .hasMessageContaining("Field 'value' must be a number greater than 0.0");

        csvContent = """
                expirationDate,value,description
                %s,-1,Conta de luz
                """.formatted(expirationDate);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(csvContent.getBytes()));

        assertThatThrownBy(() -> billCsvReadService.toBill(file))
                .isInstanceOf(DtoImportRowException.class)
                .hasMessageContaining("Field 'value' must be a number greater than 0.0");

    }

    @Test
    void read__shouldThrowExceptionWhenFileContentDataDescriptionIsNull() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("text/csv");

        String expirationDate = now().format(ofPattern("dd/MM/yyyy"));
        String csvContent = """
                expirationDate,value,description
                %s,10.0,
                """.formatted(expirationDate);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(csvContent.getBytes()));

        assertThatThrownBy(() -> billCsvReadService.toBill(file))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error parsing CSV line: 2. [%s,10.0,]".formatted(expirationDate));
    }

}