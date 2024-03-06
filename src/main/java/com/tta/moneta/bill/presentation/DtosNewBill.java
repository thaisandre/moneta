package com.tta.moneta.bill.presentation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tta.moneta.bill.domain.Bill;
import com.tta.moneta.bill.domain.BillSituation;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

class DtosNewBill {

    record DtoNewBillInput(@NotNull @FutureOrPresent @JsonFormat(pattern = "dd/MM/yyyy") LocalDate expirationDate,
                           @NotNull @Digits(integer = 50, fraction=2) @DecimalMin(value = "0.0", inclusive = false) BigDecimal value,
                           @NotBlank String description) {

        public Bill toModel() {
            return new Bill(expirationDate, value, description);
        }
    }

    record DtoNewBillOutput(Integer id,
                            @JsonFormat(pattern = "dd/MM/yyyy") LocalDate expirationDate,
                            BigDecimal value,
                            String description,
                            BillSituation situation) {
        public DtoNewBillOutput(@NotNull Bill bill) {
            this(bill.getId(),
                    bill.getExpirationDate(),
                    bill.getValue(),
                    bill.getDescription(),
                    bill.getSituation());
        }
    }
}
