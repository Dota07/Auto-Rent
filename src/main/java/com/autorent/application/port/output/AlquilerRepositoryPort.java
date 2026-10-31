package com.autorent.application.port.output;

import com.autorent.domain.model.Alquiler;
import java.util.Optional;

public interface AlquilerRepositoryPort {
    Alquiler guardar(Alquiler alquiler);
    Optional<Alquiler> buscarPorId(Long id);
    boolean existeActivoPorClienteId(Long clienteId);
}
