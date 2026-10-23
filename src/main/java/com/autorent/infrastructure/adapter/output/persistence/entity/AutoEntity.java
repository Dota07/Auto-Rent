package com.autorent.infrastructure.adapter.output.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "auto_entity")
public class AutoEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String placa;
    private String marca;
    private String modelo;
    private Integer anio;
    
    @Column(name = "precio_por_dia")
    private Double precioPorDia;
    
    // Aquí incluimos el depósito que agregamos como extra
    private Double deposito;
    
    @Enumerated(EnumType.STRING)
    private EstadoAutoEntity estado;

    public enum EstadoAutoEntity {
        DISPONIBLE, ALQUILADO, MANTENIMIENTO
    }

    public AutoEntity() {
    }

    // Getters y Setters manuales
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }
    public Double getPrecioPorDia() { return precioPorDia; }
    public void setPrecioPorDia(Double precioPorDia) { this.precioPorDia = precioPorDia; }
    public Double getDeposito() { return deposito; }
    public void setDeposito(Double deposito) { this.deposito = deposito; }
    public EstadoAutoEntity getEstado() { return estado; }
    public void setEstado(EstadoAutoEntity estado) { this.estado = estado; }
}