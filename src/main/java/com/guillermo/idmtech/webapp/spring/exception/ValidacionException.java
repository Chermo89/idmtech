package com.guillermo.idmtech.webapp.spring.exception;

public class ValidacionException extends BusinessException {
    public ValidacionException(String mensaje) {
        super("VALIDATION_ERROR", mensaje);
    }
}