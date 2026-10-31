package com.autorent.application.usecase;

import com.autorent.application.port.input.AutoService;

import com.autorent.application.port.output.AutoRepositoryPort;
import com.autorent.domain.exception.AutoNoEncontradoException;
import com.autorent.domain.model.Auto;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

public class AutoUseCase implements AutoService {
    private final AutoRepositoryPort autoRepository;

    public AutoUseCase(AutoRepositoryPort autoRepository) {
        this.autoRepository = autoRepository;
    }

    @Override
    public Auto registrar(Auto auto) {

        validarYNormalizar(auto);
        validarPlacaUnica(auto.getPlaca(), null);
        auto.setId(null); // El ID es asignado por la base de datos.
        auto.setEstado(Auto.EstadoAuto.DISPONIBLE);
        if (auto.getDeposito() == null) auto.setDeposito(300000.0);

        return autoRepository.guardar(auto);
    }

    // Edita los datos del auto. Nunca cambia el estado y no se permite si está ALQUILADO.
    @Override
    @Transactional
    public Auto actualizar(Long id, Auto datos) {
        Auto existente = buscarOFallar(id);
        if (existente.getEstado() == Auto.EstadoAuto.ALQUILADO) {
            throw new IllegalStateException("No se puede editar un auto que está ALQUILADO.");
        }
        validarYNormalizar(datos);
        validarPlacaUnica(datos.getPlaca(), id);

        existente.setPlaca(datos.getPlaca());
        existente.setMarca(datos.getMarca());
        existente.setModelo(datos.getModelo());
        existente.setAnio(datos.getAnio());
        existente.setPrecioPorDia(datos.getPrecioPorDia());
        if (datos.getDeposito() != null) existente.setDeposito(datos.getDeposito());
        return autoRepository.guardar(existente);
    }

    // Cambio manual: solo DISPONIBLE <-> MANTENIMIENTO. ALQUILADO lo gestionan los alquileres.
    @Override
    @Transactional
    public Auto cambiarEstado(Long id, Auto.EstadoAuto nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El estado es obligatorio.");
        }
        Auto auto = buscarOFallar(id);
        if (nuevoEstado == Auto.EstadoAuto.ALQUILADO) {
            throw new IllegalStateException("Ningún auto se puede marcar como ALQUILADO manualmente; "
                    + "ese estado se asigna al crear un alquiler.");
        }
        if (auto.getEstado() == Auto.EstadoAuto.ALQUILADO) {
            throw new IllegalStateException("Un auto ALQUILADO solo vuelve a DISPONIBLE al finalizar su alquiler.");
        }
        auto.setEstado(nuevoEstado);
        return autoRepository.guardar(auto);
    }

    @Override
    public Optional<Auto> buscarPorId(Long id) { return autoRepository.buscarPorId(id); }

    @Override
    public List<Auto> buscarDisponibles() { return autoRepository.buscarDisponibles(); }

    @Override
    public List<Auto> buscarTodos() { return autoRepository.buscarTodos(); }

    private Auto buscarOFallar(Long id) {
        return autoRepository.buscarPorId(id)
                .orElseThrow(() -> new AutoNoEncontradoException("El auto con ID " + id + " no existe."));
    }

    private void validarYNormalizar(Auto auto) {
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
    }

    // idActual = null al registrar; al editar se ignora el propio auto.
    private void validarPlacaUnica(String placa, Long idActual) {
        boolean repetida = autoRepository.buscarPorPlaca(placa)
                .filter(otro -> !Objects.equals(otro.getId(), idActual))
                .isPresent();
        if (repetida) {
            throw new IllegalStateException("Ya existe un auto con esa placa.");
        }
    }

    private boolean vacio(String texto) { return texto == null || texto.trim().isEmpty(); }
}
