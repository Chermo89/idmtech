package com.guillermo.idmtech.webapp.spring.exception;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}