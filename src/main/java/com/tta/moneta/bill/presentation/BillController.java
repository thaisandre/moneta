package com.tta.moneta.bill.presentation;

import com.tta.moneta.bill.domain.BillRepository;
import com.tta.moneta.bill.presentation.DtosNewBill.DtoNewBillInput;
import com.tta.moneta.bill.presentation.DtosNewBill.DtoNewBillOutput;
import com.tta.moneta.bill.presentation.DtosUpdateBill.DtoUpdateBillInput;
import com.tta.moneta.bill.presentation.DtosUpdateBill.DtoUpdateBillOutput;
import com.tta.moneta.bill.domain.Bill;
import com.tta.moneta.support.IfResourceIsFound;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

import static com.tta.moneta.utils.PaginationUtils.getDefaulPageable;
import static org.springframework.http.ResponseEntity.*;

@AllArgsConstructor
@RestController
public class BillController {

    private final BillRepository billRepository;

    @GetMapping("/bills")
    public ResponseEntity<Page<DtoBillOutputList>> list(Optional<Integer> page) {
        return ok(billRepository.findAll(getDefaulPageable(page)).map(DtoBillOutputList::new));
    }

    @GetMapping("/bills/{id}")
    public ResponseEntity<DtoBillOutput> findById(@PathVariable Long id) {
        return ok(new DtoBillOutput(IfResourceIsFound.of(billRepository.findById(id))));
    }

    @PostMapping("/bills")
    public ResponseEntity<DtoNewBillOutput> create(@Valid @RequestBody DtoNewBillInput newBillInputDto, UriComponentsBuilder uriBuilder) {
        Bill newBill = billRepository.save(newBillInputDto.toModel());
        URI location = uriBuilder.path("/bills/{id}").buildAndExpand(newBill.getId()).toUri();
        return created(location).body(new DtoNewBillOutput(newBill));
    }

    @Transactional
    @PutMapping("/bills/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody DtoUpdateBillInput updateBillInputDto) {
        Bill bill = IfResourceIsFound.of(billRepository.findById(id));
        if(bill.isPaid()) return badRequest().body(new DtoBillApiGenericError("It is not possible to edit a paid bill"));
        updateBillInputDto.updateBill(bill);
        return ok((new DtoUpdateBillOutput(bill)));
    }

    @Transactional
    @PutMapping("bills/pay/{id}")
    public ResponseEntity<?> payBill(@PathVariable Long id) {
        Bill bill = IfResourceIsFound.of(billRepository.findById(id));
        if(bill.isPaid()) return badRequest().body(new DtoBillApiGenericError("Bill already paid"));
        bill.pay();
        return ok(new DtoBillPaidOutput(bill));
    }
}
