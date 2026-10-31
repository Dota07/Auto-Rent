package com.autorent.infrastructure.adapter.input.rest;

import com.autorent.application.port.input.AlquilerService;
import com.autorent.domain.model.Alquiler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alquileres")
public class AlquilerController {
    private final AlquilerService alquilerService;

    public AlquilerController(AlquilerService alquilerService) {
        this.alquilerService = alquilerService;
    }

    @PostMapping
    public ResponseEntity<Alquiler> crearAlquiler(@RequestBody AlquilerRequest request) {
        Alquiler alquiler = alquilerService.crear(request.getClienteId(), request.getAutoId(), request.getCantidadDias());
        return ResponseEntity.status(HttpStatus.CREATED).body(alquiler);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alquiler> consultarAlquiler(@PathVariable Long id) {
        return alquilerService.buscarPorId(id)
                .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/finalizar")
    public ResponseEntity<Alquiler> finalizarAlquiler(@PathVariable Long id) {
        return ResponseEntity.ok(alquilerService.finalizar(id));
    }

    // Clase sencilla para leer el JSON del POST; no recibe valorTotal.
    public static class AlquilerRequest {
        private Long clienteId;
        private Long autoId;
        private Integer cantidadDias;

        public Long getClienteId() { return clienteId; }
        public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
        public Long getAutoId() { return autoId; }
        public void setAutoId(Long autoId) { this.autoId = autoId; }
        public Integer getCantidadDias() { return cantidadDias; }
        public void setCantidadDias(Integer cantidadDias) { this.cantidadDias = cantidadDias; }
    }
}
