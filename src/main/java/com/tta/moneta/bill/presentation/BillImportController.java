package com.tta.moneta.bill.presentation;

import com.tta.moneta.bill.domain.Bill;
import com.tta.moneta.bill.domain.BillRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
public class BillImportController {

    private final BillRepository billRepository;
    private final BillCsvReadService billCsvReadService;
    private final BillImportFileValidator billImportFileValidator;

    @InitBinder("dtoImportBills")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(billImportFileValidator);
    }

    @PostMapping("/bills/import")
    public ResponseEntity importBill(@Valid DtoImportBills dtoImportBills) {
        List<Bill> bills = billCsvReadService.toBill(dtoImportBills.file());
        billRepository.saveAll(bills);
        return ResponseEntity.ok(bills);
    }
}
