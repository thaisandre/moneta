package com.tta.moneta.support;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
public class HealthCheckController {

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ok().build();
    }
}
