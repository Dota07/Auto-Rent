package com.autorent.infrastructure.adapter.output.persistence.adapter;

import com.autorent.application.port.output.AlquilerRepositoryPort;
import com.autorent.application.port.output.TrazaPort;
import com.autorent.domain.model.Alquiler;
import com.autorent.infrastructure.adapter.output.persistence.entity.AlquilerEntity;
import com.autorent.infrastructure.adapter.output.persistence.repository.AlquilerJpaRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class AlquilerPersistenceAdapter implements AlquilerRepositoryPort {

    private final AlquilerJpaRepository repository;
    private final TrazaPort traza;

    public AlquilerPersistenceAdapter(AlquilerJpaRepository repository, TrazaPort traza) {
        this.repository = repository;
        this.traza = traza;
    }

    @Override
    public Alquiler guardar(Alquiler alquiler) {
        traza.registrar("node-port-out", "AlquilerRepositoryPort.guardar");
        traza.registrar("node-adapter-out", "AlquilerPersistenceAdapter: guardar alquiler con JPA/H2");
        AlquilerEntity entity = new AlquilerEntity();
        entity.setId(alquiler.getId());
        entity.setClienteId(alquiler.getClienteId());
        entity.setAutoId(alquiler.getAutoId());
        entity.setFechaInicio(alquiler.getFechaInicio());
        entity.setFechaFin(alquiler.getFechaFin());
        entity.setCantidadDias(alquiler.getCantidadDias());
        entity.setValorTotal(alquiler.getValorTotal());
        
        if (alquiler.getEstado() != null) {
            entity.setEstado(AlquilerEntity.EstadoAlquilerEntity.valueOf(alquiler.getEstado().name()));
        }
        
        AlquilerEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Alquiler> buscarPorId(Long id) {
        traza.registrar("node-port-out", "AlquilerRepositoryPort.buscarPorId");
        traza.registrar("node-adapter-out", "AlquilerPersistenceAdapter: consultar alquiler en H2");
        return repository.findById(id).map(this::toDomain);
    }

    private Alquiler toDomain(AlquilerEntity entity) {
        Alquiler alquiler = new Alquiler();
        alquiler.setId(entity.getId());
        alquiler.setClienteId(entity.getClienteId());
        alquiler.setAutoId(entity.getAutoId());
        alquiler.setFechaInicio(entity.getFechaInicio());
        alquiler.setFechaFin(entity.getFechaFin());
        alquiler.setCantidadDias(entity.getCantidadDias());
        alquiler.setValorTotal(entity.getValorTotal());
        
        if (entity.getEstado() != null) {
            alquiler.setEstado(Alquiler.EstadoAlquiler.valueOf(entity.getEstado().name()));
        }
        return alquiler;
    }
}