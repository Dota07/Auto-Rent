package com.autorent.application.usecase;

import com.autorent.application.port.input.AlquilerService;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;
import com.autorent.application.port.output.AlquilerRepositoryPort;
import com.autorent.application.port.output.AutoRepositoryPort;
import com.autorent.application.port.output.ClienteRepositoryPort;
import com.autorent.application.port.output.TrazaPort;
import com.autorent.domain.exception.AutoNoDisponibleException;
import com.autorent.domain.exception.AutoNoEncontradoException;
import com.autorent.domain.exception.ClienteNoEncontradoException;
import com.autorent.domain.exception.AlquilerNoEncontradoException;
import com.autorent.domain.model.Alquiler;
import com.autorent.domain.model.Auto;

// El caso de uso implementa el puerto de entrada y coordina reglas y guardados.
// JPA y H2 quedan fuera de esta clase, detrás de los puertos de salida.
public class AlquilerUseCase implements AlquilerService {
    private final AlquilerRepositoryPort alquilerRepository;
    private final AutoRepositoryPort autoRepository;
    private final ClienteRepositoryPort clienteRepository;
    private final TrazaPort traza;

    public AlquilerUseCase(AlquilerRepositoryPort alquilerRepository, AutoRepositoryPort autoRepository,
                           ClienteRepositoryPort clienteRepository, TrazaPort traza) {
        this.alquilerRepository = alquilerRepository;
        this.autoRepository = autoRepository;
        this.clienteRepository = clienteRepository;
        this.traza = traza;
    }

    @Override
    @Transactional
    public Alquiler crear(Long clienteId, Long autoId, Integer cantidadDias) {
        traza.registrar("node-port-in", "AlquilerService.crear: puerto de entrada → AlquilerUseCase");
        if (clienteId == null || autoId == null || cantidadDias == null || cantidadDias <= 0) {
            throw new IllegalArgumentException("Cliente, auto y cantidad de días (> 0) son obligatorios.");
        }

        // Validar la existencia del cliente y el estado del vehículo.
        clienteRepository.buscarPorId(clienteId)
                .orElseThrow(() -> new ClienteNoEncontradoException("El cliente con ID " + clienteId + " no existe."));
        Auto auto = autoRepository.buscarPorId(autoId)
                .orElseThrow(() -> new AutoNoEncontradoException("El auto con ID " + autoId + " no existe."));
        if (auto.getEstado() != Auto.EstadoAuto.DISPONIBLE) {
            throw new AutoNoDisponibleException("El auto se encuentra " + auto.getEstado() + " y no puede ser alquilado.");
        }
        traza.registrar("node-domain", "AlquilerUseCase: cliente existe y auto DISPONIBLE");

        LocalDate inicio = LocalDate.now();
        Alquiler alquiler = new Alquiler(null, clienteId, autoId, inicio,
                inicio.plusDays(cantidadDias), cantidadDias,
                auto.getPrecioPorDia() * cantidadDias, Alquiler.EstadoAlquiler.ACTIVO);
        traza.registrar("node-domain", "AlquilerUseCase: calcular total y crear alquiler ACTIVO");

        // Ambos guardados forman UNA transacción: si alguno falla, se revierten ambos.
        auto.setEstado(Auto.EstadoAuto.ALQUILADO);
        autoRepository.guardar(auto);
        return alquilerRepository.guardar(alquiler);
    }

    @Override
    @Transactional
    public Alquiler finalizar(Long alquilerId) {
        traza.registrar("node-port-in", "AlquilerService.finalizar: puerto de entrada → AlquilerUseCase");
        Alquiler alquiler = buscarPorId(alquilerId)
                .orElseThrow(() -> new AlquilerNoEncontradoException("El alquiler con ID " + alquilerId + " no existe."));
        if (alquiler.getEstado() == Alquiler.EstadoAlquiler.FINALIZADO) {
            throw new IllegalStateException("El alquiler ya se encuentra finalizado.");
        }
        Auto auto = autoRepository.buscarPorId(alquiler.getAutoId())
                .orElseThrow(() -> new AutoNoEncontradoException("El auto asociado al alquiler no existe."));
        alquiler.setEstado(Alquiler.EstadoAlquiler.FINALIZADO);
        auto.setEstado(Auto.EstadoAuto.DISPONIBLE);
        traza.registrar("node-domain", "AlquilerUseCase: alquiler FINALIZADO y auto DISPONIBLE");
        autoRepository.guardar(auto);
        return alquilerRepository.guardar(alquiler);
    }

    @Override
    public Optional<Alquiler> buscarPorId(Long id) { return alquilerRepository.buscarPorId(id); }
}
