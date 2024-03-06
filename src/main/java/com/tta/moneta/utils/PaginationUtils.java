package com.tta.moneta.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public class PaginationUtils {

    public static Pageable getDefaulPageable(Optional<Integer> page) {
        return PageRequest.of(page.orElse(0), 20);
    }
}
