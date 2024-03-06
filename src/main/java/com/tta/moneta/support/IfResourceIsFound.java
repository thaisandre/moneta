package com.tta.moneta.support;

import com.tta.moneta.support.exception.ResourceNotFoundException;

import java.util.Optional;

import static org.springframework.util.Assert.notNull;

public class IfResourceIsFound {

    public static <T> T of(Optional<T> optional) {
        notNull(optional, "optional cannot be null");
        return optional.orElseThrow(() -> new ResourceNotFoundException());
    }
}
