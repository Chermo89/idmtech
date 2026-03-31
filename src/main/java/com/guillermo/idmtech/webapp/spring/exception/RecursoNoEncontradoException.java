package com.guillermo.idmtech.webapp.spring.exception;

public class RecursoNoEncontradoException extends BusinessException {
    public RecursoNoEncontradoException(String mensaje) {
        super("NOT_FOUND", mensaje);
    }
}
