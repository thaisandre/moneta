package com.tta.moneta.bill.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.tta.moneta.bill.domain.BillSituation.PAY;
import static com.tta.moneta.bill.domain.BillSituation.PENDING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.math.BigDecimal.ROUND_HALF_UP;
import static java.time.LocalDateTime.now;
import static org.springframework.util.Assert.*;

@Entity
public class Bill {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;
    private LocalDateTime createdAt = now();
    private LocalDateTime updatedAt;
    private LocalDate expirationDate;
    private LocalDateTime paymentDate;
    private BigDecimal value;
    private String description;
    @Enumerated(EnumType.STRING)
    private BillSituation situation = PENDING;

    @Deprecated
    public Bill() {}

    public Bill(LocalDate expirationDate, BigDecimal value, String description) {
        validateExpirationDate(expirationDate);
        validateValue(value);
        validateDescription(description);
        this.expirationDate = expirationDate;
        this.value = value.setScale(2, ROUND_HALF_UP);
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public BigDecimal getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public BillSituation getSituation() {
        return situation;
    }

    public void pay() {
        isTrue(!isPaid(), "Bill already paid");
        this.situation = PAY;
        this.paymentDate = now();
        this.updatedAt = now();
    }

    public void update(LocalDate expirationDate, BigDecimal value, String description) {
        validateExpirationDate(expirationDate);
        validateValue(value);
        validateDescription(description);
        isTrue(!isPaid(), "It is not possible to edit a paid bill");
        this.expirationDate = expirationDate;
        this.value = value.setScale(2, ROUND_HALF_UP);
        this.description = description;
        this.updatedAt = now();
    }

    public boolean isPaid() {
        return PAY.equals(this.situation);
    }

    private void validateExpirationDate(LocalDate expirationDate) {
        notNull(expirationDate, "expirationDate is required");
        isTrue(expirationDate.isEqual(LocalDate.now()) || expirationDate.isAfter(LocalDate.now()), "expirationDate must be equal or after today");
    }

    private void validateValue(BigDecimal value) {
        notNull(value, "value is required");
        isTrue(value.compareTo(BigDecimal.ZERO) > 0, "value must be greater than zero");
    }

    private void validateDescription(String description) {
        hasText(description, "description is required");
    }
}
