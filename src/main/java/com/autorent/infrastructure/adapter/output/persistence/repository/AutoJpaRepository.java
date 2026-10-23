package com.autorent.infrastructure.adapter.output.persistence.repository;

import com.autorent.infrastructure.adapter.output.persistence.entity.AutoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutoJpaRepository extends JpaRepository<AutoEntity, Long> {
    // Spring Data JPA crea esta consulta automáticamente
    List<AutoEntity> findByEstado(AutoEntity.EstadoAutoEntity estado);
}