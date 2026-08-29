package com.example.onlyflans.dto;

import java.time.LocalDate;

public class OrdenRequest {
    private String telefono;
    private String nombre;
    private int cantidad;
    private LocalDate fechaDeseada;

    // Genera los Getters y Setters con IntelliJ (Alt + Insert)
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public LocalDate getFechaDeseada() { return fechaDeseada; }
    public void setFechaDeseada(LocalDate fechaDeseada) { this.fechaDeseada = fechaDeseada; }
}