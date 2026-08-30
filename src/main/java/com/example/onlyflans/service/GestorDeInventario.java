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

        if ("INMEDIATA".equals(modalidadOBloque)) {
            fechaAsignada = LocalDate.now();
            horaAsignada = LocalTime.of(23, 59);
        } else {
            // Nuevo motor de parseo dinámico (Ej: "2026-08-31|10:00")
            String[] partes = modalidadOBloque.split("\\|");
            if (partes.length != 2) {
                throw new IllegalArgumentException("Formato de bloque inválido. Esperado YYYY-MM-DD|HH:mm");
            }

            fechaAsignada = LocalDate.parse(partes[0]);
            horaAsignada = LocalTime.parse(partes[1], DateTimeFormatter.ofPattern("HH:mm"));
        }

        // Patrón Upsert de lotes
        Lote lote = loteRepository.findByFechaAndHoraCorte(fechaAsignada, horaAsignada)
                .orElseGet(() -> {
                    Lote nuevoLote = new Lote();
                    nuevoLote.setFecha(fechaAsignada);
                    nuevoLote.setHoraCorte(horaAsignada);
                    return loteRepository.save(nuevoLote);
                });

        lote.reservarUnidades(cantidad);

        return loteRepository.save(lote);
    }
}