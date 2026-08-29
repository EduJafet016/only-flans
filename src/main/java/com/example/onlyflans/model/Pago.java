package com.example.onlyflans.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación Uno a Uno: Un pago pertenece estrictamente a una orden y viceversa
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_id", nullable = false, unique = true)
    private Orden orden;

    @Column(name = "referencia_mp", nullable = false, unique = true)
    private String referenciaMp;

    @Column(nullable = false)
    private String estado;

    @Column(name = "fecha_confirmacion", nullable = false)
    private LocalDateTime fechaConfirmacion = LocalDateTime.now();

    public Pago() {}

    public Pago(Orden orden, String referenciaMp, String estado) {
        this.orden = orden;
        this.referenciaMp = referenciaMp;
        this.estado = estado;
    }

    // Genera los Getters y Setters con IntelliJ (Alt + Insert)
    public Long getId() { return id; }
    public Orden getOrden() { return orden; }
    public void setOrden(Orden orden) { this.orden = orden; }
    public String getReferenciaMp() { return referenciaMp; }
    public void setReferenciaMp(String referenciaMp) { this.referenciaMp = referenciaMp; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDateTime getFechaConfirmacion() { return fechaConfirmacion; }
    public void setFechaConfirmacion(LocalDateTime fechaConfirmacion) { this.fechaConfirmacion = fechaConfirmacion; }
}