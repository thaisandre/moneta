package com.tta.moneta.bill.presentation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tta.moneta.bill.domain.Bill;
import com.tta.moneta.bill.domain.BillSituation;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

class DtosUpdateBill {

    record DtoUpdateBillInput(@NotNull @FutureOrPresent @JsonFormat(pattern = "dd/MM/yyyy") LocalDate expirationDate,
                              @NotNull @Digits(integer = 50, fraction=2) @DecimalMin(value = "0.0", inclusive = false) BigDecimal value,
                              @NotBlank String description) {

        public void updateBill(Bill bill) {
            bill.update(expirationDate, value, description);
        }
    }

    record DtoUpdateBillOutput(Integer id,
                               @JsonFormat(pattern = "dd/MM/yyyy") LocalDate expirationDate,
                               BigDecimal value,
                               String description,
                               BillSituation situation,
                               @JsonFormat(pattern = "dd/MM/yyyy") LocalDateTime paymentDate) {
        public DtoUpdateBillOutput(Bill bill) {
            this(bill.getId(),
                    bill.getExpirationDate(),
                    bill.getValue(),
                    bill.getDescription(),
                    bill.getSituation(),
                    bill.getPaymentDate());
        }
    }
}
