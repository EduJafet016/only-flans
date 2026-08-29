package com.example.onlyflans.repository;

import com.example.onlyflans.model.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrdenRepository extends JpaRepository<Orden, Long> {
    // Para el dashboard: consultar órdenes confirmadas
    List<Orden> findByEstado(Orden.EstadoOrden estado);
}