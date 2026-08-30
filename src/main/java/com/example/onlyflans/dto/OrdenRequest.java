package com.example.onlyflans.dto;

public class OrdenRequest {
    private String telefono;
    private String nombre;
    private int cantidad;
    private String fechaDeseada; // Soporta "INMEDIATA" o los bloques como "HOY-10:00"

    // Getters requeridos por OrdenService
    public String getTelefono() {
        return telefono;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public String getFechaDeseada() {
        return fechaDeseada;
    }

    // Setters requeridos por Jackson para la deserialización (el IDE puede marcarlos como no usados por reflexión)
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public void setFechaDeseada(String fechaDeseada) {
        this.fechaDeseada = fechaDeseada;
    }
}