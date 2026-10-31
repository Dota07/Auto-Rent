package com.autorent.infrastructure.adapter.output.persistence.adapter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.autorent.application.port.output.AutoRepositoryPort;
import com.autorent.domain.model.Auto;
import com.autorent.infrastructure.adapter.output.persistence.entity.AutoEntity;
import com.autorent.infrastructure.adapter.output.persistence.repository.AutoJpaRepository;

@Component
public class AutoPersistenceAdapter implements AutoRepositoryPort {

    private final AutoJpaRepository autoJpaRepository;

    public AutoPersistenceAdapter(AutoJpaRepository autoJpaRepository) {
        this.autoJpaRepository = autoJpaRepository;
    }

    @Override
    public Auto guardar(Auto auto) {


        AutoEntity entity = toEntity(auto);
        AutoEntity savedEntity = autoJpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Auto> buscarPorId(Long id) {


        return autoJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Auto> buscarPorPlaca(String placa) {
        return autoJpaRepository.findByPlacaIgnoreCase(placa).map(this::toDomain);
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