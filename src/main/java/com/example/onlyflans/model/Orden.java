package com.example.onlyflans.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ordenes")
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lote_id")
    private Lote lote;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoOrden estado;

    @Column(name = "monto", precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "cantidad")
    private Integer cantidad;

    @Column(name = "fecha_deseada")
    private String fechaDeseada;
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private java.time.LocalDateTime fechaCreacion;

    // Enum interno para el estado de la orden
    public enum EstadoOrden {
        PENDIENTE,
        PAGADO,
        ENTREGADO,
        CANCELADO
    }

    // Constructor vacío requerido por JPA (Hibernate)
    public Orden() {
    }

    // --- Getters y Setters ---
    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = java.time.LocalDateTime.now();
    }
    public java.time.LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(java.time.LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Lote getLote() {
        return lote;
    }

    public void setLote(Lote lote) {
        this.lote = lote;
    }

    public EstadoOrden getEstado() {
        return estado;
    }

    public void setEstado(EstadoOrden estado) {
        this.estado = estado;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getFechaDeseada() {
        return fechaDeseada;
    }

    public void setFechaDeseada(String fechaDeseada) {
        this.fechaDeseada = fechaDeseada;
    }
}

