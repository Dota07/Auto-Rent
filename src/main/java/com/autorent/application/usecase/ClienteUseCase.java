package com.autorent.application.usecase;

import com.autorent.application.port.input.ClienteService;

import com.autorent.application.port.output.AlquilerRepositoryPort;
import com.autorent.application.port.output.ClienteRepositoryPort;
import com.autorent.domain.exception.ClienteNoEncontradoException;
import com.autorent.domain.model.Cliente;
import java.util.Objects;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

public class ClienteUseCase implements ClienteService {
    private final ClienteRepositoryPort clienteRepository;
    private final AlquilerRepositoryPort alquilerRepository;

    public ClienteUseCase(ClienteRepositoryPort clienteRepository, AlquilerRepositoryPort alquilerRepository) {
        this.clienteRepository = clienteRepository;
        this.alquilerRepository = alquilerRepository;
    }

    @Override
    @Transactional
    public Cliente registrar(Cliente cliente) {

        validarYNormalizar(cliente);
        validarUnicos(cliente, null);
        cliente.setId(null); // No aceptar el ID del usuario para registros nuevos.

        return clienteRepository.guardar(cliente);
    }

    // No se puede editar un cliente que tenga un alquiler ACTIVO.
    @Override
    @Transactional
    public Cliente actualizar(Long id, Cliente datos) {
        Cliente existente = clienteRepository.buscarPorId(id)
                .orElseThrow(() -> new ClienteNoEncontradoException("El cliente con ID " + id + " no existe."));
        if (alquilerRepository.existeActivoPorClienteId(id)) {
            throw new IllegalStateException("No se puede editar un cliente con un alquiler ACTIVO.");
        }
        validarYNormalizar(datos);
        validarUnicos(datos, id);

        existente.setNombre(datos.getNombre());
        existente.setDocumento(datos.getDocumento());
        existente.setTelefono(datos.getTelefono());
        existente.setEmail(datos.getEmail());
        return clienteRepository.guardar(existente);
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) { return clienteRepository.buscarPorId(id); }

    private void validarYNormalizar(Cliente cliente) {
        if (cliente == null || vacio(cliente.getNombre()) || vacio(cliente.getDocumento())) {
            throw new IllegalArgumentException("Nombre y documento son obligatorios.");
        }
        if (vacio(cliente.getEmail()) || !cliente.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("El correo electrónico no es válido.");
        }
        cliente.setNombre(cliente.getNombre().trim());
        cliente.setDocumento(cliente.getDocumento().trim());
        cliente.setEmail(cliente.getEmail().trim());
    }

    // idActual = null al registrar; al editar se ignora el propio cliente.
    private void validarUnicos(Cliente cliente, Long idActual) {
        if (clienteRepository.buscarPorDocumento(cliente.getDocumento())
                .filter(otro -> !Objects.equals(otro.getId(), idActual)).isPresent()) {
            throw new IllegalStateException("Ya existe un cliente con ese documento.");
        }
        if (clienteRepository.buscarPorEmail(cliente.getEmail())
                .filter(otro -> !Objects.equals(otro.getId(), idActual)).isPresent()) {
            throw new IllegalStateException("Ya existe un cliente con ese email.");
        }
    }

    private boolean vacio(String texto) { return texto == null || texto.trim().isEmpty(); }
}
