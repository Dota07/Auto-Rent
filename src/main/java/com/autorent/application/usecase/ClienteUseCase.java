package com.autorent.application.usecase;

import com.autorent.application.port.input.ClienteService;

import com.autorent.application.port.output.ClienteRepositoryPort;
import com.autorent.application.port.output.TrazaPort;
import com.autorent.domain.model.Cliente;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

public class ClienteUseCase implements ClienteService {
    private final ClienteRepositoryPort clienteRepository;
    private final TrazaPort traza;

    public ClienteUseCase(ClienteRepositoryPort clienteRepository, TrazaPort traza) {
        this.clienteRepository = clienteRepository;
        this.traza = traza;
    }

    @Override
    @Transactional
    public Cliente registrar(Cliente cliente) {
        traza.registrar("node-port-in", "ClienteService.registrar: puerto de entrada → ClienteUseCase");
        if (cliente == null || vacio(cliente.getNombre()) || vacio(cliente.getDocumento())) {
            throw new IllegalArgumentException("Nombre y documento son obligatorios.");
        }
        if (vacio(cliente.getEmail()) || !cliente.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("El correo electrónico no es válido.");
        }
        cliente.setNombre(cliente.getNombre().trim());
        cliente.setDocumento(cliente.getDocumento().trim());
        cliente.setEmail(cliente.getEmail().trim());
        if (clienteRepository.existePorDocumento(cliente.getDocumento())) {
            throw new IllegalStateException("Ya existe un cliente con ese documento.");
        }
        cliente.setId(null); // No aceptar el ID del usuario para registros nuevos.
        traza.registrar("node-domain", "ClienteUseCase: datos válidos y documento no repetido");
        return clienteRepository.guardar(cliente);
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) { return clienteRepository.buscarPorId(id); }

    private boolean vacio(String texto) { return texto == null || texto.trim().isEmpty(); }
}
