package com.autorent.application.port.output;

import com.autorent.domain.model.Auto;
import java.util.List;
import java.util.Optional;

public interface AutoRepositoryPort {
    Auto guardar(Auto auto);
    Optional<Auto> buscarPorId(Long id);
    Optional<Auto> buscarPorPlaca(String placa);
    List<Auto> buscarDisponibles();
    List<Auto> buscarTodos();
}
