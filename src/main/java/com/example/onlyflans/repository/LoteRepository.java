package com.example.onlyflans.repository;

import com.example.onlyflans.model.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Long> {

    List<Lote> findByFechaOrderByHoraCorteAsc(LocalDate fecha);

    // Firma requerida por GestorDeInventario para resolver la existencia del lote
    Optional<Lote> findByFechaAndHoraCorte(LocalDate fecha, LocalTime horaCorte);
}