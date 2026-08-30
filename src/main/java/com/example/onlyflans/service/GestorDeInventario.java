package com.example.onlyflans.service;

import com.example.onlyflans.model.Lote;
import com.example.onlyflans.repository.LoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
public class GestorDeInventario {

    private final LoteRepository loteRepository;

    public GestorDeInventario(LoteRepository loteRepository) {
        this.loteRepository = loteRepository;
    }

    @Transactional
    public Lote asignarLoteDisponible(String modalidadOBloque, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }

        LocalDate fechaAsignada;
        LocalTime horaAsignada;

        // 1. Parseo y transformación de tipos
        if ("INMEDIATA".equals(modalidadOBloque)) {
            fechaAsignada = LocalDate.now();
            horaAsignada = LocalTime.of(23, 59); // Lote lógico diario para el stock de entrega inmediata
        } else {
            // Descomposición del string "HOY-10:00" o "MANANA-17:00"
            String[] partes = modalidadOBloque.split("-");
            if (partes.length != 2) {
                throw new IllegalArgumentException("Formato de bloque inválido: " + modalidadOBloque);
            }

            fechaAsignada = partes[0].equals("HOY") ? LocalDate.now() : LocalDate.now().plusDays(1);
            horaAsignada = LocalTime.parse(partes[1], DateTimeFormatter.ofPattern("HH:mm"));
        }

        // 2. Patrón Upsert: Buscar en DB o crear dinámicamente si no existe
        Lote lote = loteRepository.findByFechaAndHoraCorte(fechaAsignada, horaAsignada)
                .orElseGet(() -> {
                    Lote nuevoLote = new Lote();
                    nuevoLote.setFecha(fechaAsignada);
                    nuevoLote.setHoraCorte(horaAsignada);
                    // Persistir el lote vacío primero para que Hibernate le asigne un ID (Primary Key)
                    return loteRepository.save(nuevoLote);
                });

        // 3. Mutación del estado y control de concurrencia (@Version)
        lote.reservarUnidades(cantidad);

        return loteRepository.save(lote);
    }
}