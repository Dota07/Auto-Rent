package com.autorent.infrastructure.adapter.output.persistence.adapter;

import com.autorent.application.port.output.AlquilerRepositoryPort;
import com.autorent.domain.model.Alquiler;
import com.autorent.infrastructure.adapter.output.persistence.entity.AlquilerEntity;
import com.autorent.infrastructure.adapter.output.persistence.repository.AlquilerJpaRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class AlquilerPersistenceAdapter implements AlquilerRepositoryPort {

    private final AlquilerJpaRepository repository;

    public AlquilerPersistenceAdapter(AlquilerJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Alquiler guardar(Alquiler alquiler) {


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


        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public boolean existeActivoPorClienteId(Long clienteId) {
        return repository.existsByClienteIdAndEstado(clienteId, AlquilerEntity.EstadoAlquilerEntity.ACTIVO);
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