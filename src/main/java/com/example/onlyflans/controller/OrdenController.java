package com.example.onlyflans.controller;

import com.example.onlyflans.dto.OrdenRequest;
import com.example.onlyflans.model.Lote;
import com.example.onlyflans.model.Orden;
import com.example.onlyflans.repository.LoteRepository;
import com.example.onlyflans.repository.OrdenRepository;
import com.example.onlyflans.service.OrdenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ordenes")
public class OrdenController {

    private final OrdenService ordenService;
    private final OrdenRepository ordenRepository;
    private final LoteRepository loteRepository;

    public OrdenController(OrdenService ordenService, OrdenRepository ordenRepository, LoteRepository loteRepository) {
        this.ordenService = ordenService;
        this.ordenRepository = ordenRepository;
        this.loteRepository = loteRepository;
    }

    @PostMapping
    public ResponseEntity<?> crearOrden(@RequestBody OrdenRequest request) {
        try {
            String urlPago = ordenService.procesarNuevaOrden(request);
            return ResponseEntity.ok(urlPago);
        } catch (IllegalStateException e) {
            // 409 Conflict: El usuario intenta reservar más de lo que permite el lote
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error interno del servidor"));
        }
    }

    @GetMapping
    public ResponseEntity<List<Orden>> obtenerTodasLasOrdenes() {
        return ResponseEntity.ok(ordenRepository.findAll());
    }

    @GetMapping("/disponibilidad")
    public ResponseEntity<List<Lote>> obtenerDisponibilidad() {
        return ResponseEntity.ok(loteRepository.findAll());
    }

    // Endpoint para liberar el stock de inmediato si el usuario cancela en el frontend
    @PostMapping("/cancelar-reciente")
    public ResponseEntity<?> cancelarOrdenInmediata(@RequestBody Map<String, String> payload) {
        String telefono = payload.get("telefono");

        // Uso del tipado estricto (Enum) y el JOIN dinámico por nombre de método
        Orden ordenReciente = ordenRepository.findFirstByClienteTelefonoAndEstadoOrderByFechaCreacionDesc(telefono, Orden.EstadoOrden.PENDIENTE);

        if (ordenReciente != null) {
            Lote lote = ordenReciente.getLote();
            if (lote != null) {
                // Se devuelve el stock a la cocina
                lote.setUnidadesReservadas(lote.getUnidadesReservadas() - ordenReciente.getCantidad());
                loteRepository.save(lote);
            }
            // Se mata la orden
            ordenReciente.setEstado(Orden.EstadoOrden.CANCELADA);
            ordenRepository.save(ordenReciente);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> recibirWebhookMercadoPago(
            @RequestParam(value = "type", required = false) String tipo,
            @RequestBody Map<String, Object> payload) {

        if (tipo == null && payload.containsKey("type")) {
            tipo = payload.get("type").toString();
        }

        if ("payment".equals(tipo)) {
            Object rawData = payload.get("data");
            if (rawData instanceof Map<?, ?> rawMap) {
                Object idObj = rawMap.get("id");
                if (idObj != null) {
                    ordenService.procesarNotificacionPago(idObj.toString());
                }
            }
        }
        return ResponseEntity.ok("EVENT_RECEIVED");
    }
}