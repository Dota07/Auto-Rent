package com.autorent.infrastructure.adapter.input.rest;

import com.autorent.application.port.output.TrazaPort;
import com.autorent.domain.exception.AlquilerNoEncontradoException;
import com.autorent.domain.exception.AutoNoDisponibleException;
import com.autorent.domain.exception.AutoNoEncontradoException;
import com.autorent.domain.exception.ClienteNoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final TrazaPort traza;

    public GlobalExceptionHandler(TrazaPort traza) { this.traza = traza; }

    @ExceptionHandler({AutoNoEncontradoException.class, ClienteNoEncontradoException.class,
            AlquilerNoEncontradoException.class})
    public ResponseEntity<Map<String, Object>> noEncontrado(RuntimeException ex, HttpServletRequest request) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler({AutoNoDisponibleException.class, IllegalStateException.class,
            DataIntegrityViolationException.class})
    public ResponseEntity<Map<String, Object>> conflicto(RuntimeException ex, HttpServletRequest request) {
        String mensaje = ex instanceof DataIntegrityViolationException
                ? "El dato ya existe o incumple una restricción de la base de datos." : ex.getMessage();
        return error(HttpStatus.CONFLICT, mensaje, request);
    }

    @ExceptionHandler({IllegalArgumentException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<Map<String, Object>> solicitudInvalida(Exception ex, HttpServletRequest request) {
        String mensaje = ex instanceof HttpMessageNotReadableException
                ? "El cuerpo JSON contiene datos incorrectos o no se puede leer." : ex.getMessage();
        return error(HttpStatus.BAD_REQUEST, mensaje, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> errorInterno(Exception ex, HttpServletRequest request) {
        logger.error("Error no esperado en AutoRent", ex);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor.", request);
    }

    private ResponseEntity<Map<String, Object>> error(HttpStatus estado, String mensaje,
                                                       HttpServletRequest request) {
        traza.registrar("node-domain", "Respuesta HTTP " + estado.value() + ": " + mensaje);
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("error", mensaje);
        if ("true".equalsIgnoreCase(request.getParameter("traza"))) {
            respuesta.put("pasos", traza.obtenerPasos());
        }
        return ResponseEntity.status(estado).body(respuesta);
    }
}
