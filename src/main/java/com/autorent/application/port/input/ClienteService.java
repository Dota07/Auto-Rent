package com.autorent.application.port.input;

import com.autorent.domain.model.Cliente;
import java.util.Optional;

// Puerto de entrada: expone las operaciones para clientes.
public interface ClienteService {
    Cliente registrar(Cliente cliente);
    Optional<Cliente> buscarPorId(Long id);
}
