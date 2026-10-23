package com.autorent.infrastructure.adapter.output.persistence.adapter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.autorent.application.port.output.AutoRepositoryPort;
import com.autorent.application.port.output.TrazaPort;
import com.autorent.domain.model.Auto;
import com.autorent.infrastructure.adapter.output.persistence.entity.AutoEntity;
import com.autorent.infrastructure.adapter.output.persistence.repository.AutoJpaRepository;

@Component
public class AutoPersistenceAdapter implements AutoRepositoryPort {

    private final AutoJpaRepository autoJpaRepository;
    private final TrazaPort traza;

    public AutoPersistenceAdapter(AutoJpaRepository autoJpaRepository, TrazaPort traza) {
        this.autoJpaRepository = autoJpaRepository;
        this.traza = traza;
    }

    @Override
    public Auto guardar(Auto auto) {
        traza.registrar("node-port-out", "AutoRepositoryPort.guardar");
        traza.registrar("node-adapter-out", "AutoPersistenceAdapter: guardar auto con JPA/H2");
        AutoEntity entity = toEntity(auto);
        AutoEntity savedEntity = autoJpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Auto> buscarPorId(Long id) {
        traza.registrar("node-port-out", "AutoRepositoryPort.buscarPorId");
        traza.registrar("node-adapter-out", "AutoPersistenceAdapter: consultar auto en H2");
        return autoJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Auto> buscarDisponibles() {
        return autoJpaRepository.findByEstado(AutoEntity.EstadoAutoEntity.DISPONIBLE)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Auto> buscarTodos() {
        return autoJpaRepository.findAll()
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    // MÉTODOS DE MAPEO (Convertir de Dominio a Entidad y viceversa)
    private AutoEntity toEntity(Auto auto) {
        AutoEntity entity = new AutoEntity();
        entity.setId(auto.getId());
        entity.setPlaca(auto.getPlaca());
        entity.setMarca(auto.getMarca());
        entity.setModelo(auto.getModelo());
        entity.setAnio(auto.getAnio());
        entity.setPrecioPorDia(auto.getPrecioPorDia());
        entity.setDeposito(auto.getDeposito());
        
        if (auto.getEstado() != null) {
            entity.setEstado(AutoEntity.EstadoAutoEntity.valueOf(auto.getEstado().name()));
        }
        return entity;
    }

    private Auto toDomain(AutoEntity entity) {
        Auto auto = new Auto();
        auto.setId(entity.getId());
        auto.setPlaca(entity.getPlaca());
        auto.setMarca(entity.getMarca());
        auto.setModelo(entity.getModelo());
        auto.setAnio(entity.getAnio());
        auto.setPrecioPorDia(entity.getPrecioPorDia());
        auto.setDeposito(entity.getDeposito());
        
        if (entity.getEstado() != null) {
            auto.setEstado(Auto.EstadoAuto.valueOf(entity.getEstado().name()));
        }
        return auto;
    }
}