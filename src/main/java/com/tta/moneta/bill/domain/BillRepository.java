package com.tta.moneta.bill.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Long>, JpaSpecificationExecutor<Bill> {

    Page<Bill> findAll(Pageable pageable);

    Page<Bill> findAll(Specification specification, Pageable pageable);

    @Query("SELECT SUM(b.value) FROM Bill b WHERE b.paymentDate BETWEEN :start AND :end")
    Optional<Integer> sumValueByPaymentDateBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
