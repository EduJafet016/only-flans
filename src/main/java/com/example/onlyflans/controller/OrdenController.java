package com.example.onlyflans.controller;

import com.example.onlyflans.dto.OrdenRequest;
import com.example.onlyflans.model.Orden;
import com.example.onlyflans.repository.OrdenRepository;
import com.example.onlyflans.service.OrdenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ordenes")
public class OrdenController {

    private final OrdenService ordenService;
    private final OrdenRepository ordenRepository;

    public OrdenController(OrdenService ordenService, OrdenRepository ordenRepository) {
        this.ordenService = ordenService;
        this.ordenRepository = ordenRepository;
    }

    // Endpoint para registrar la orden y generar el pago
    @PostMapping
    public ResponseEntity<String> crearOrden(@RequestBody OrdenRequest request) {
        String urlPago = ordenService.procesarNuevaOrden(request);
        return ResponseEntity.ok(urlPago);
    }

    // Endpoint GET para que la WebApp del panel liste todas las órdenes (pendientes y pagadas)
    @GetMapping
    public ResponseEntity<List<Orden>> obtenerTodasLasOrdenes() {
        List<Orden> ordenes = ordenRepository.findAll();
        return ResponseEntity.ok(ordenes);
    }

    // Endpoint Webhook para MercadoPago
    @PostMapping("/webhook")
    public ResponseEntity<String> recibirWebhookMercadoPago(
            @RequestParam(value = "type", required = false) String tipo,
            @RequestBody Map<String, Object> payload) {

        // MercadoPago a veces envía el tipo dentro del JSON o como parámetro request
        if (tipo == null && payload.containsKey("type")) {
            tipo = payload.get("type").toString();
        }

        if ("payment".equals(tipo)) {
            Object rawData = payload.get("data");
            if (rawData instanceof Map<?, ?> rawMap) {
                Object idObj = rawMap.get("id");
                if (idObj != null) {
                    String paymentId = idObj.toString();
                    // Procesamos el pago y actualizamos el estado de la orden en la BD
                    ordenService.procesarNotificacionPago(paymentId);
                }
            }
        }

        return ResponseEntity.ok("EVENT_RECEIVED");
    }
}