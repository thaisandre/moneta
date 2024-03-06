package com.tta.moneta.bill.presentation;

import com.tta.moneta.bill.presentation.DtosBillsFilter.DtoBillFilterByPeriod;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
class TotalPaidBillFilterValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return DtoBillFilterByPeriod.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if(errors.hasErrors()) return;
        DtoBillFilterByPeriod newBillInputDto = (DtoBillFilterByPeriod) target;

        if(newBillInputDto.endDate().isBefore(newBillInputDto.startDate())) {
            errors.reject( "endDate.before.startDate", "endDate must be after startDate");
        }
    }
}
