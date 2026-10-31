package com.autorent.infrastructure.adapter.input.rest;

import com.autorent.domain.exception.AlquilerNoEncontradoException;
import com.autorent.domain.exception.AutoNoDisponibleException;
import com.autorent.domain.exception.AutoNoEncontradoException;
import com.autorent.domain.exception.ClienteNoEncontradoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({AutoNoEncontradoException.class, ClienteNoEncontradoException.class,
            AlquilerNoEncontradoException.class})
    public ResponseEntity<Map<String, String>> noEncontrado(RuntimeException ex) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({AutoNoDisponibleException.class, IllegalStateException.class,
            DataIntegrityViolationException.class})
    public ResponseEntity<Map<String, String>> conflicto(RuntimeException ex) {
        String mensaje = ex instanceof DataIntegrityViolationException
                ? "El dato ya existe o incumple una restricción de la base de datos." : ex.getMessage();
        return error(HttpStatus.CONFLICT, mensaje);
    }

    @ExceptionHandler({IllegalArgumentException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<Map<String, String>> solicitudInvalida(Exception ex) {
        String mensaje = ex instanceof HttpMessageNotReadableException
                ? "El cuerpo JSON contiene datos incorrectos o no se puede leer." : ex.getMessage();
        return error(HttpStatus.BAD_REQUEST, mensaje);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> errorInterno(Exception ex) {
        logger.error("Error no esperado en AutoRent", ex);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor.");
    }

    private ResponseEntity<Map<String, String>> error(HttpStatus estado, String mensaje) {
        return ResponseEntity.status(estado).body(Map.of("error", mensaje));
    }
}
