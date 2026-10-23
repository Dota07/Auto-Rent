package com.autorent.application.usecase;

import com.autorent.application.port.input.AutoService;

import com.autorent.application.port.output.AutoRepositoryPort;
import com.autorent.application.port.output.TrazaPort;
import com.autorent.domain.model.Auto;
import java.util.List;
import java.util.Optional;

public class AutoUseCase implements AutoService {
    private final AutoRepositoryPort autoRepository;
    private final TrazaPort traza;

    public AutoUseCase(AutoRepositoryPort autoRepository, TrazaPort traza) {
        this.autoRepository = autoRepository;
        this.traza = traza;
    }

    @Override
    public Auto registrar(Auto auto) {
        traza.registrar("node-port-in", "AutoService.registrar: puerto de entrada → AutoUseCase");
        if (auto == null || vacio(auto.getPlaca()) || vacio(auto.getMarca()) || vacio(auto.getModelo())) {
            throw new IllegalArgumentException("Placa, marca y modelo son obligatorios.");
        }
        if (auto.getPrecioPorDia() == null || !Double.isFinite(auto.getPrecioPorDia())
                || auto.getPrecioPorDia() <= 0) {
            throw new IllegalArgumentException("El precio por día debe ser mayor que cero.");
        }
        if (auto.getAnio() == null || auto.getAnio() <= 0) {
            throw new IllegalArgumentException("El año del auto debe ser válido.");
        }
        if (auto.getDeposito() != null && (!Double.isFinite(auto.getDeposito()) || auto.getDeposito() < 0)) {
            throw new IllegalArgumentException("El depósito no puede ser negativo.");
        }
        auto.setPlaca(auto.getPlaca().trim());
        auto.setMarca(auto.getMarca().trim());
        auto.setModelo(auto.getModelo().trim());
        auto.setId(null); // El ID es asignado por la base de datos.
        auto.setEstado(Auto.EstadoAuto.DISPONIBLE);
        if (auto.getDeposito() == null) auto.setDeposito(300000.0);
        traza.registrar("node-domain", "AutoUseCase: datos válidos; estado DISPONIBLE");
        return autoRepository.guardar(auto);
    }

    @Override
    public Optional<Auto> buscarPorId(Long id) { return autoRepository.buscarPorId(id); }

    @Override
    public List<Auto> buscarDisponibles() { return autoRepository.buscarDisponibles(); }

    @Override
    public List<Auto> buscarTodos() { return autoRepository.buscarTodos(); }

    private boolean vacio(String texto) { return texto == null || texto.trim().isEmpty(); }
}
