package com.autorent.infrastructure.adapter.input.rest;

import com.autorent.application.port.input.AutoService;
import com.autorent.application.port.output.TrazaPort;
import com.autorent.domain.model.Auto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/autos")
public class AutoController {
    private final AutoService autoService;
    private final TrazaPort traza;

    public AutoController(AutoService autoService, TrazaPort traza) {
        this.autoService = autoService;
        this.traza = traza;
    }

    @PostMapping
    public ResponseEntity<?> crearAuto(@RequestBody Auto auto,
                                        @RequestParam(name = "traza", defaultValue = "false") boolean traza) {
        this.traza.registrar("node-adapter-in", "AutoController: POST /api/autos recibido");
        Auto nuevoAuto = autoService.registrar(auto);
        if (traza) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("resultado", nuevoAuto, "pasos", this.traza.obtenerPasos()));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoAuto);
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
}
