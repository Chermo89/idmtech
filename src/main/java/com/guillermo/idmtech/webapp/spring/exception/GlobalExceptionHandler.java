package com.guillermo.idmtech.webapp.spring.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.dao.DataAccessException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Excepción de negocio personalizada
    @ExceptionHandler(BusinessException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleBusiness(BusinessException ex) {
        ErrorResponse error = new ErrorResponse(
                ex.getCode(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        HttpStatus status = ex instanceof RecursoNoEncontradoException ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
        return Mono.just(ResponseEntity.status(status).body(error));
    }

    // Errores de validación de Spring (Bean Validation)
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidation(WebExchangeBindException ex) {
        List<String> detalles = ex.getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());
        ErrorResponse error = new ErrorResponse(
                "VALIDATION_FAILED",
                "Errores de validación en la petición",
                LocalDateTime.now(),
                detalles
        );
        return Mono.just(ResponseEntity.badRequest().body(error));
    }

    // Errores de acceso a datos (R2DBC)
    @ExceptionHandler(DataAccessException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleDataAccess(DataAccessException ex) {
        log.error("Error de acceso a datos", ex);  // <-- ahora log está definido
        ErrorResponse error = new ErrorResponse(
                "DATA_ACCESS_ERROR",
                "Error interno en la base de datos",
                LocalDateTime.now()
        );
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error));
    }

    // Cualquier otra excepción no controlada
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGeneric(Exception ex) {
        log.error("Error no controlado", ex);
        ErrorResponse error = new ErrorResponse(
                "INTERNAL_ERROR",
                "Ha ocurrido un error inesperado",
                LocalDateTime.now()
        );
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error));
    }

    // Clase interna para la respuesta de error
    public static class ErrorResponse {
        private String code;
        private String message;
        private LocalDateTime timestamp;
        private List<String> details;

        // Constructor con 3 parámetros
        public ErrorResponse(String code, String message, LocalDateTime timestamp) {
            this.code = code;
            this.message = message;
            this.timestamp = timestamp;
        }

        // Constructor con 4 parámetros (incluye detalles)
        public ErrorResponse(String code, String message, LocalDateTime timestamp, List<String> details) {
            this.code = code;
            this.message = message;
            this.timestamp = timestamp;
            this.details = details;
        }

        // Getters y setters (necesarios para la serialización JSON)
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        public List<String> getDetails() { return details; }
        public void setDetails(List<String> details) { this.details = details; }
    }
}