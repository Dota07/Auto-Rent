package com.autorent.domain.model;

import java.time.LocalDate;

public class Alquiler {
    private Long id;
    private Long clienteId;
    private Long autoId;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer cantidadDias;
    private Double valorTotal;
    private EstadoAlquiler estado;

    public enum EstadoAlquiler {
        ACTIVO, FINALIZADO
    }

    public Alquiler() {}

    public Long getId() {
        return id;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public Long getAutoId() {
        return autoId;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public Integer getCantidadDias() {
        return cantidadDias;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public EstadoAlquiler getEstado() {
        return estado;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public void setAutoId(Long autoId) {
        this.autoId = autoId;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public void setCantidadDias(Integer cantidadDias) {
        this.cantidadDias = cantidadDias;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public void setEstado(EstadoAlquiler estado) {
        this.estado = estado;
    }

    public Alquiler(Long id, Long clienteId, Long autoId, LocalDate fechaInicio, LocalDate fechaFin, Integer cantidadDias, Double valorTotal, EstadoAlquiler estado) {
        this.id = id;
        this.clienteId = clienteId;
        this.autoId = autoId;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.cantidadDias = cantidadDias;
        this.valorTotal = valorTotal;
        this.estado = estado;
    }
}