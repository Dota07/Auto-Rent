package com.autorent.infrastructure.adapter.input.rest;

import com.autorent.application.port.input.AlquilerService;
import com.autorent.application.port.output.TrazaPort;
import com.autorent.domain.model.Alquiler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/alquileres")
public class AlquilerController {
    private final AlquilerService alquilerService;
    private final TrazaPort traza;

    public AlquilerController(AlquilerService alquilerService, TrazaPort traza) {
        this.alquilerService = alquilerService;
        this.traza = traza;
    }

    @PostMapping
    public ResponseEntity<?> crearAlquiler(@RequestBody AlquilerRequest request,
                                            @RequestParam(name = "traza", defaultValue = "false") boolean traza) {
        this.traza.registrar("node-adapter-in", "AlquilerController: POST /api/alquileres recibido");
        Alquiler alquiler = alquilerService.crear(request.getClienteId(), request.getAutoId(), request.getCantidadDias());
        if (traza) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("resultado", alquiler, "pasos", this.traza.obtenerPasos()));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(alquiler);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alquiler> consultarAlquiler(@PathVariable Long id) {
        return alquilerService.buscarPorId(id)
                .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/finalizar")
    public ResponseEntity<?> finalizarAlquiler(@PathVariable Long id,
                                               @RequestParam(name = "traza", defaultValue = "false") boolean traza) {
        this.traza.registrar("node-adapter-in", "AlquilerController: PUT /api/alquileres/" + id + "/finalizar recibido");
        Alquiler alquiler = alquilerService.finalizar(id);
        if (traza) return ResponseEntity.ok(Map.of("resultado", alquiler, "pasos", this.traza.obtenerPasos()));
        return ResponseEntity.ok(alquiler);
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
