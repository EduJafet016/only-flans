package com.example.onlyflans.service;

import com.example.onlyflans.model.Lote;
import com.example.onlyflans.repository.LoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class GestorDeInventario {

    private final LoteRepository loteRepository;

    // Inyección de dependencias por constructor
    public GestorDeInventario(LoteRepository loteRepository) {
        this.loteRepository = loteRepository;
    }

    @Transactional
    public Lote asignarLoteDisponible(LocalDate fecha, int cantidadRequerida) {
        List<Lote> lotesDelDia = loteRepository.findByFechaOrderByHoraCorteAsc(fecha);

        for (Lote lote : lotesDelDia) {
            if (lote.hayEspacio(cantidadRequerida)) {
                lote.reservarUnidades(cantidadRequerida);
                return loteRepository.save(lote); // El commit evaluará el @Version de JPA
            }
        }

        throw new RuntimeException("Sold out: Capacidad máxima de producción alcanzada para esta fecha. Gracias por su Preferencia");
    }
}