package com.autorent.application.port.output;

import com.autorent.domain.model.Cliente;
import java.util.Optional;

public interface ClienteRepositoryPort {
    Cliente guardar(Cliente cliente);
    Optional<Cliente> buscarPorId(Long id);
    Optional<Cliente> buscarPorDocumento(String documento);
    Optional<Cliente> buscarPorEmail(String email);
}
