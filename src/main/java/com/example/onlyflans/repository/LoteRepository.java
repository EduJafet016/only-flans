package com.example.onlyflans.repository;

import com.example.onlyflans.model.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Long> {
    List<Lote> findByFechaOrderByHoraCorteAsc(LocalDate fecha);
}