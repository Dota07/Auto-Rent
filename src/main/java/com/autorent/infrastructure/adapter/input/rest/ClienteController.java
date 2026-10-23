package com.autorent.infrastructure.adapter.input.rest;

import com.autorent.application.port.input.ClienteService;
import com.autorent.application.port.output.TrazaPort;
import com.autorent.domain.model.Cliente;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    private final ClienteService clienteService;
    private final TrazaPort traza;

    public ClienteController(ClienteService clienteService, TrazaPort traza) {
        this.clienteService = clienteService;
        this.traza = traza;
    }

    @PostMapping
    public ResponseEntity<?> crearCliente(@RequestBody Cliente cliente,
                                           @RequestParam(name = "traza", defaultValue = "false") boolean traza) {
        this.traza.registrar("node-adapter-in", "ClienteController: POST /api/clientes recibido");
        Cliente nuevoCliente = clienteService.registrar(cliente);
        if (traza) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("resultado", nuevoCliente, "pasos", this.traza.obtenerPasos()));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> consultarCliente(@PathVariable Long id) {
        return clienteService.buscarPorId(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
