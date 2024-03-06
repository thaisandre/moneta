package com.tta.moneta.bill.presentation;

import com.tta.moneta.bill.domain.BillRepository;
import com.tta.moneta.bill.presentation.DtosBillsFilter.DtoBillFilterByExpirationDateAndDescription;
import com.tta.moneta.bill.presentation.DtosBillsFilter.DtoBillFilterByPeriod;
import com.tta.moneta.bill.presentation.DtosBillsFilter.DtoBillFilterByPeriodOutput;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.tta.moneta.utils.PaginationUtils.getDefaulPageable;
import static org.springframework.http.ResponseEntity.ok;

@AllArgsConstructor
@RestController
public class BillFilterController {

    private final BillRepository billRepository;
    private final TotalPaidBillFilterValidator totalPaidBillFilterValidator;

    @InitBinder("totalPaidBillFilter")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(totalPaidBillFilterValidator);
    }

    @GetMapping("/bills/filter")
    public ResponseEntity<Page<DtoBillOutputList>> billsToPay(DtoBillFilterByExpirationDateAndDescription filter, Optional<Integer> page) {
       return ok(billRepository.findAll(filter.getSpecification(), getDefaulPageable(page))
               .map(DtoBillOutputList::new));
    }

    @GetMapping("/bills/total/period")
    public ResponseEntity<DtoBillFilterByPeriodOutput> calculateTotalPaid(@Valid DtoBillFilterByPeriod filter) {
        return ok(new DtoBillFilterByPeriodOutput(
                    billRepository.sumValueByPaymentDateBetween(filter.getStartDateTime(), filter.getEndDateTime())
                            .orElse(0)));
    }
}
