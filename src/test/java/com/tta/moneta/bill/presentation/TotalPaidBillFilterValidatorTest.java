package com.tta.moneta.bill.presentation;

import com.tta.moneta.bill.presentation.DtosBillsFilter.DtoBillFilterByPeriod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;


class TotalPaidBillFilterValidatorTest {

    private TotalPaidBillFilterValidator totalPaidBillFilterValidator;

    @BeforeEach
    void setUp() {
        totalPaidBillFilterValidator = spy(TotalPaidBillFilterValidator.class);
    }

    @Test
    void supports__shouldReturnTrueWhenClazzIsInstanceOfDtoBillFilterByPeriod() {
        assertThat(totalPaidBillFilterValidator.supports(DtoBillFilterByPeriod.class)).isTrue();
    }

    @Test
    void supports__shouldReturnFalseWhenClazzIsNotInstanceOfDtoBillFilterByPeriod() {
        assertThat(totalPaidBillFilterValidator.supports(Object.class)).isFalse();
    }

    @Test
    void validate__shouldNotAddErrorWhenStartDateIsEqualEndDate() {
        LocalDate date = LocalDate.of(2024, 3, 4);
        DtoBillFilterByPeriod target = new DtoBillFilterByPeriod(date, date);
        BindingResult result = new BeanPropertyBindingResult(target, "dtoBillFilterByPeriod");

        totalPaidBillFilterValidator.validate(target, result);
        assertThat(result.getAllErrors()).isEmpty();
    }

    @Test
    void validate__shouldNotAddErrorErrorWhenStartDateIsBeforeEndDate() {
        LocalDate date = LocalDate.of(2024, 3, 4);
        DtoBillFilterByPeriod target = new DtoBillFilterByPeriod(date, date.plusDays(1));
        BindingResult result = new BeanPropertyBindingResult(target, "dtoBillFilterByPeriod");

        totalPaidBillFilterValidator.validate(target, result);
        assertThat(result.getAllErrors()).isEmpty();
    }

    @Test
    void validate__shouldAddErrorsWhenStartDateIsAfterEndDate() {
        LocalDate date = LocalDate.of(2024, 3, 4);
        DtoBillFilterByPeriod target = new DtoBillFilterByPeriod(date.plusDays(1), date);
        BindingResult result = new BeanPropertyBindingResult(target, "dtoBillFilterByPeriod");

        totalPaidBillFilterValidator.validate(target, result);
        assertThat(result.getAllErrors()).hasSize(1)
                .extracting("defaultMessage").contains("endDate must be after startDate");
    }
}