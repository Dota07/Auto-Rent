package com.autorent.infrastructure.adapter.output.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "alquiler_entity")
public class AlquilerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long clienteId;
    private Long autoId;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer cantidadDias;
    private Double valorTotal;
    
    @Enumerated(EnumType.STRING)
    private EstadoAlquilerEntity estado;

    public enum EstadoAlquilerEntity { ACTIVO, FINALIZADO }

    public AlquilerEntity() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public Long getAutoId() { return autoId; }
    public void setAutoId(Long autoId) { this.autoId = autoId; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    public Integer getCantidadDias() { return cantidadDias; }
    public void setCantidadDias(Integer cantidadDias) { this.cantidadDias = cantidadDias; }
    public Double getValorTotal() { return valorTotal; }
    public void setValorTotal(Double valorTotal) { this.valorTotal = valorTotal; }
    public EstadoAlquilerEntity getEstado() { return estado; }
    public void setEstado(EstadoAlquilerEntity estado) { this.estado = estado; }
}