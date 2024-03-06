package com.tta.moneta.utils;

import com.tta.moneta.bill.domain.Bill;
import com.tta.moneta.bill.presentation.DtoBillImportRow;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.time.LocalDate.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.mock;

class CsvUtilsTest {

    @Test
    void read__shouldThrowExceptionWhenInputStreamIsNull() {
        assertThatThrownBy(() -> CsvUtils.read(null, DtoBillImportRow.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("inputStream is required");
    }

    @Test
    void read__shouldThrowExceptionWhenClazzIsNull() {
        assertThatThrownBy(() -> CsvUtils.read(mock(InputStream.class), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("clazz is required");
    }

    @Test
    void read__shouldThrowExceptionWhenHeaderNotMatchWithClazzAttributes() {
        InputStream emptyInputStream = new ByteArrayInputStream("".getBytes());
        assertThatThrownBy(() -> CsvUtils.read(emptyInputStream, DtoBillImportRow.class))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Error capturing CSV header!");

        String csvContent = "data1,data2,data3";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
        assertThatThrownBy(() -> CsvUtils.read(inputStream, DtoBillImportRow.class))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Error capturing CSV header!");
    }

    @Test
    void read__shouldReturnListOfDtoBillImportRowWithDataRowInFile() {
        String expirationDate = now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String csvContent = """
            expirationDate,value,description
            %s,100.00,Conta de luz
            %s,200.00,Conta de água
            """.formatted(expirationDate, expirationDate);
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
        List<DtoBillImportRow> dtoBillImportRows = CsvUtils.read(inputStream, DtoBillImportRow.class);
        assertThat(dtoBillImportRows).hasSize(2);
        assertThat(dtoBillImportRows.stream().map(DtoBillImportRow::toBill))
                .extracting(Bill::getExpirationDate, Bill::getValue, Bill::getDescription)
                .containsExactly(tuple(now(), new BigDecimal("100.00"), "Conta de luz"),
                        tuple(now(), new BigDecimal("200.00"), "Conta de água"));
    }

}