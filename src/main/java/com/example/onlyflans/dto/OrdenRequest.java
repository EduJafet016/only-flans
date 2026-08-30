package com.example.onlyflans.dto;

public record OrdenRequest(
        String telefono,
        String nombre,
        int cantidad,
        String fechaDeseada
) {}