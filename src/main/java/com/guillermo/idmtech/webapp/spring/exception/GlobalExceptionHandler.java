package com.guillermo.idmtech.webapp.spring.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleBusiness(BusinessException ex) {

        return Mono.fromSupplier(() -> {
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(error);
        });
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Map<String, String>>> handleGeneral(Exception ex) {

        return Mono.fromSupplier(() -> {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error interno");
            return ResponseEntity.status(500).body(error);
        });
    }
}