package com.autorent.application.port.input;

import com.autorent.domain.model.Alquiler;
import java.util.Optional;

// Puerto de entrada: operaciones para crear, finalizar y consultar alquileres.
public interface AlquilerService {
    Alquiler crear(Long clienteId, Long autoId, Integer cantidadDias);
    Alquiler finalizar(Long alquilerId);
    Optional<Alquiler> buscarPorId(Long id);
}
