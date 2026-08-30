package com.example.onlyflans.repository;

import com.example.onlyflans.model.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrdenRepository extends JpaRepository<Orden, Long> {

    // Para el dashboard: consultar órdenes confirmadas
    List<Orden> findByEstado(Orden.EstadoOrden estado);

    // Para el StockSweeperService (Cron Job): Busca órdenes abandonadas
    List<Orden> findByEstadoAndFechaCreacionBefore(Orden.EstadoOrden estado, LocalDateTime limite);

    // Para el OrdenController (Cancelación rápida): Usa ClienteTelefono para el JOIN automático
    Orden findFirstByClienteTelefonoAndEstadoOrderByFechaCreacionDesc(String telefono, Orden.EstadoOrden estado);
}