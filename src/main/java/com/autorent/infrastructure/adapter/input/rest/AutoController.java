package com.autorent.infrastructure.adapter.input.rest;

import com.autorent.application.port.input.AutoService;
import com.autorent.domain.model.Auto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/autos")
public class AutoController {
    private final AutoService autoService;

    public AutoController(AutoService autoService) {
        this.autoService = autoService;
    }

    @PostMapping
    public ResponseEntity<Auto> crearAuto(@RequestBody Auto auto) {
        Auto nuevoAuto = autoService.registrar(auto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoAuto);
    }

    // Edita datos del auto (no cambia el estado; no permitido si está ALQUILADO).
    @PutMapping("/{id}")
    public ResponseEntity<Auto> actualizarAuto(@PathVariable Long id, @RequestBody Auto auto) {
        return ResponseEntity.ok(autoService.actualizar(id, auto));
    }

    // Cambio manual de estado: solo DISPONIBLE <-> MANTENIMIENTO.
    @PutMapping("/{id}/estado")
    public ResponseEntity<Auto> cambiarEstado(@PathVariable Long id, @RequestBody EstadoRequest request) {
        return ResponseEntity.ok(autoService.cambiarEstado(id, request.getEstado()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Auto> consultarAuto(@PathVariable Long id) {
        return autoService.buscarPorId(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<Auto>> consultarDisponibles() {
        return ResponseEntity.ok(autoService.buscarDisponibles());
    }

    @GetMapping
    public ResponseEntity<List<Auto>> consultarTodos() {
        return ResponseEntity.ok(autoService.buscarTodos());
    }

    // Clase sencilla para leer el JSON del cambio de estado: {"estado": "MANTENIMIENTO"}
    public static class EstadoRequest {
        private Auto.EstadoAuto estado;

        public Auto.EstadoAuto getEstado() { return estado; }
        public void setEstado(Auto.EstadoAuto estado) { this.estado = estado; }
    }
}
