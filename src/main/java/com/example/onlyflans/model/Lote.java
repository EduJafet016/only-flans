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

    // Se elimina 'final' para permitir actualización dinámica desde el admin panel
    @Column(name = "capacidad_max", nullable = false)
    private Integer capacidadMax = 3;

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

    // Getters y Setters
    public Long getId() { return id; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHoraCorte() { return horaCorte; }
    public void setHoraCorte(LocalTime horaCorte) { this.horaCorte = horaCorte; }

    public Integer getCapacidadMax() { return capacidadMax; }
    public void setCapacidadMax(Integer capacidadMax) { this.capacidadMax = capacidadMax; }

    public Integer getUnidadesReservadas() { return unidadesReservadas; }

    // FIX: Mutador necesario para liberar el stock desde el controlador y el Cron Job
    public void setUnidadesReservadas(Integer unidadesReservadas) {
        this.unidadesReservadas = unidadesReservadas;
    }

    public Long getVersion() { return version; }
}