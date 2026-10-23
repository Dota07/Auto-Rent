package com.autorent.application.port.output;

import com.autorent.domain.model.Cliente;
import java.util.Optional;

public interface ClienteRepositoryPort {
    Cliente guardar(Cliente cliente);
    Optional<Cliente> buscarPorId(Long id);
    boolean existePorDocumento(String documento);
}
