package com.example.onlyflans.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "lotes")
public class Lote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_corte", nullable = false)
    private LocalTime horaCorte;

    // Límite físico estricto de 3 unidades por bloque de producción
    @Column(name = "capacidad_max", nullable = false)
    private final Integer capacidadMax = 3;

    @Column(name = "unidades_reservadas", nullable = false)
    private Integer unidadesReservadas = 0;

    // Control transaccional de concurrencia a nivel de base de datos
    @Version
    private Long version;

    public Lote() {}

    public boolean hayEspacio(int cantidadSolicitada) {
        return (this.unidadesReservadas + cantidadSolicitada) <= this.capacidadMax;
    }

    public void reservarUnidades(int cantidad) {
        if (!hayEspacio(cantidad)) {
            throw new IllegalStateException("Lote sin capacidad suficiente.");
        }
        this.unidadesReservadas += cantidad;
    }

    // Genera los Getters y Setters con IntelliJ (Alt + Insert)
    public Long getId() { return id; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public LocalTime getHoraCorte() { return horaCorte; }
    public void setHoraCorte(LocalTime horaCorte) { this.horaCorte = horaCorte; }
    public Integer getCapacidadMax() { return capacidadMax; }
    public Integer getUnidadesReservadas() { return unidadesReservadas; }
    public Long getVersion() { return version; }
}