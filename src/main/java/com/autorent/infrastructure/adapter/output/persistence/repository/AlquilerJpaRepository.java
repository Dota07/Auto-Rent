package com.autorent.infrastructure.adapter.output.persistence.repository;

import com.autorent.infrastructure.adapter.output.persistence.entity.AlquilerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.autorent.infrastructure.adapter.output.persistence.entity.AlquilerEntity.EstadoAlquilerEntity;

@Repository
public interface AlquilerJpaRepository extends JpaRepository<AlquilerEntity, Long> {
    boolean existsByClienteIdAndEstado(Long clienteId, EstadoAlquilerEntity estado);
}