package com.autorent.domain.model;

public class Auto {
    private Long id;
    private String placa; // Campo requerido por las especificaciones
    private String marca;
    private String modelo;
    private Integer anio;
    private Double precioPorDia;
    private Double deposito;
    private EstadoAuto estado;

    public enum EstadoAuto {
        DISPONIBLE, ALQUILADO, MANTENIMIENTO
    }

    public Auto() {}

    public Auto(Long id, String placa, String marca, String modelo, Integer anio, Double precioPorDia, Double deposito, EstadoAuto estado) {
        this.id = id;
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.precioPorDia = precioPorDia;
        this.deposito = deposito;
        this.estado = estado;
    }

    // Getters y Setters
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
    public EstadoAuto getEstado() { return estado; }
    public void setEstado(EstadoAuto estado) { this.estado = estado; }
}