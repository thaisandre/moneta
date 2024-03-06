package com.tta.moneta.bill.presentation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tta.moneta.bill.domain.Bill;
import com.tta.moneta.bill.domain.BillSituation;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

record DtoBillPaidOutput(
        Integer id,
        @JsonFormat(pattern = "dd/MM/yyyy") LocalDate expirationDate,
        BigDecimal value,
        String description,
        BillSituation situation,
        @JsonFormat(pattern = "dd/MM/yyyy") LocalDateTime paymentDate) {

        public DtoBillPaidOutput(@NotNull Bill bill) {
                this(bill.getId(),
                        bill.getExpirationDate(),
                        bill.getValue(),
                        bill.getDescription(),
                        bill.getSituation(),
                        bill.getPaymentDate());
        }
}
