package com.example.onlyflans.model;

import jakarta.persistence.*;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String telefono;

    @Column(nullable = false)
    private String nombre;

    public Cliente() {}

    public Cliente(String telefono, String nombre) {
        this.telefono = telefono;
        this.nombre = nombre;
    }

    // Genera los Getters y Setters con IntelliJ (Alt + Insert)
    public Long getId() { return id; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}