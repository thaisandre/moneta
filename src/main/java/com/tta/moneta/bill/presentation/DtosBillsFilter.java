package com.tta.moneta.bill.presentation;

import com.tta.moneta.bill.domain.Bill;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.tta.moneta.bill.domain.BillSituation.PENDING;

class DtosBillsFilter {

    public record DtoBillFilterByExpirationDateAndDescription(
            @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate expirationDate,
            String description){

        public Specification<Bill> getSpecification() {
            return (root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                if(expirationDate != null) predicates.add(criteriaBuilder.equal(root.get("expirationDate"), expirationDate));
                if(description != null) predicates.add(criteriaBuilder.equal(root.get("description"), description));
                predicates.add(criteriaBuilder.equal(root.get("situation"), PENDING));
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };
        }
    }

    record DtoBillFilterByPeriod(
            @NotNull @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate startDate,
            @NotNull @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate endDate) {

        public LocalDateTime getStartDateTime() {
            return Optional.ofNullable(startDate).map(LocalDate::atStartOfDay).orElseThrow();
        }

        public LocalDateTime getEndDateTime() {
            return Optional.ofNullable(endDate).map(date -> date.atTime(LocalTime.MAX)).orElseThrow();
        }
    }

    record DtoBillFilterByPeriodOutput(Integer totalPaid) {}
}
