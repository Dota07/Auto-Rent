package com.autorent.application.port.input;

import com.autorent.domain.model.Auto;
import java.util.List;
import java.util.Optional;

// Puerto de entrada: define lo que puede solicitar el Controller.
public interface AutoService {
    Auto registrar(Auto auto);
    Optional<Auto> buscarPorId(Long id);
    List<Auto> buscarDisponibles();
    List<Auto> buscarTodos();
}
