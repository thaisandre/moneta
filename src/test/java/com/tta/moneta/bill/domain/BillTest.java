package com.tta.moneta.bill.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static java.time.LocalDate.now;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class BillTest {

    @Test
    void constructor__shouldThrowExceptionWhenExpirationDateIsNull() {
        assertThatThrownBy(() -> new Bill(null, BigDecimal.TEN, "content"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("expirationDate is required");
    }

    @Test
    void constructor__shouldThrowExceptionWhenValueIsNull() {
        assertThatThrownBy(() -> new Bill(now(), null, "content"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value is required");
    }

    @Test
    void constructor__shouldThrowExceptionWhenValueIsEqualOrLessThanZero() {
        assertThatThrownBy(() -> new Bill(now(), BigDecimal.ZERO, "content"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be greater than zero");

        assertThatThrownBy(() -> new Bill(now(), BigDecimal.valueOf(-1L), "content"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be greater than zero");
    }

    @Test
    void constructor__shouldThrowExceptionWhenDescriptionIsNullOrEmpty() {
        assertThatThrownBy(() -> new Bill(now(), BigDecimal.TEN, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("description is required");

        assertThatThrownBy(() -> new Bill(now(), BigDecimal.TEN, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("description is required");
    }

    @Test
    void pay__shouldThrowExceptionWhenBillIsPaid() {
        Bill bill = new Bill(now(), BigDecimal.TEN, "content");
        bill.pay();
        assertThatThrownBy(bill::pay)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Bill already paid");
    }

    @Test
    void update__shouldThrowExceptionWhenExpirationDateIsNull() {
        Bill bill = new Bill(now(), BigDecimal.TEN, "content");
        assertThatThrownBy(() -> bill.update(null, BigDecimal.TEN, "content"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("expirationDate is required");
    }

    @Test
    void update__shouldThrowExceptionWhenValueIsNull() {
        Bill bill = new Bill(now(), BigDecimal.TEN, "content");
        assertThatThrownBy(() -> bill.update(now(), null, "content"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value is required");
    }

    @Test
    void update__shouldThrowExceptionWhenValueIsEqualOrLessThanZero() {
        Bill bill = new Bill(now(), BigDecimal.TEN, "content");
        assertThatThrownBy(() -> bill.update(now(), BigDecimal.ZERO, "content"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be greater than zero");

        assertThatThrownBy(() -> bill.update(now(), BigDecimal.valueOf(-1L), "content"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be greater than zero");
    }

    @Test
    void update__shouldThrowExceptionWhenDescriptionIsNullOrEmpty() {
        Bill bill = new Bill(now(), BigDecimal.TEN, "content");
        assertThatThrownBy(() -> bill.update(now(), BigDecimal.TEN, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("description is required");

        assertThatThrownBy(() -> bill.update(now(), BigDecimal.TEN, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("description is required");
    }

    @Test
    void update__shouldThrowExceptionWhenBillIsPaid() {
        Bill bill = new Bill(now(), BigDecimal.TEN, "content");
        bill.pay();
        assertThatThrownBy(() -> bill.update(now(), BigDecimal.TEN, "content"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("It is not possible to edit a paid bill");
    }

}