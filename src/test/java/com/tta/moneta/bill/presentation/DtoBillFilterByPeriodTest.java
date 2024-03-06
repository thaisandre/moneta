package com.tta.moneta.bill.presentation;

import com.tta.moneta.bill.presentation.DtosBillsFilter.DtoBillFilterByPeriod;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import static java.time.LocalDate.now;
import static java.time.LocalDateTime.MAX;
import static java.time.LocalDateTime.MIN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class DtoBillFilterByPeriodTest {

    @Test
    void getStartDateTime__shouldThrowExceptionWhenStartDateIsNull() {
        DtoBillFilterByPeriod dtoBillFilterByPeriod = new DtoBillFilterByPeriod(null, now());
        assertThatThrownBy(() -> dtoBillFilterByPeriod.getStartDateTime())
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void getStartDateTime__shouldReturnStartDateInStartDateTime() {
        LocalDate startDate = LocalDate.of(2024, 3, 4);
        DtoBillFilterByPeriod dtoBillFilterByPeriod = new DtoBillFilterByPeriod(startDate, now());
        assertThat(dtoBillFilterByPeriod.getStartDateTime())
                .isEqualTo(LocalDateTime.of(startDate, MIN.toLocalTime()));
    }

    @Test
    void getStartEndTime__shouldThrowExceptionWhenEndDateIsNull() {
        DtoBillFilterByPeriod dtoBillFilterByPeriod = new DtoBillFilterByPeriod(now(), null);
        assertThatThrownBy(() -> dtoBillFilterByPeriod.getEndDateTime())
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void getStartDateTime__shouldReturnEndDateInEndDateTime() {
        LocalDate endDate = LocalDate.of(2024, 3, 4);
        DtoBillFilterByPeriod dtoBillFilterByPeriod = new DtoBillFilterByPeriod(now(), endDate);
        assertThat(dtoBillFilterByPeriod.getEndDateTime())
                .isEqualTo(LocalDateTime.of(endDate, MAX.toLocalTime()));
    }

}