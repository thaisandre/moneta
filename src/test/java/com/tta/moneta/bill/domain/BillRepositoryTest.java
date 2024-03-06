package com.tta.moneta.bill.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BillRepositoryTest {

    @Autowired private BillRepository billRepository;

    @Test
    void sumValueByPaymentDateBetween__shouldReturnTotalPaidByPeriod() {
        Bill notPayedBill1 = new Bill(LocalDate.now(), BigDecimal.ONE, "description");

        Bill payedBill1 = new Bill(LocalDate.now(), BigDecimal.TEN, "description");
        payedBill1.pay();

        Bill payedBill2 = new Bill(LocalDate.now(), BigDecimal.TWO, "description");
        payedBill2.pay();

        billRepository.saveAll(List.of(notPayedBill1, payedBill1, payedBill2));

        LocalDateTime start = now().minus(1, DAYS);
        LocalDateTime end = now().plus(1, DAYS);

        Optional<Integer> value = billRepository.sumValueByPaymentDateBetween(start, end);
        assertThat(value).isEqualTo(Optional.of(12));

        value = billRepository.sumValueByPaymentDateBetween(start.plus(10, DAYS), end.plus(20, DAYS));
        assertThat(value).isEmpty();
    }
}