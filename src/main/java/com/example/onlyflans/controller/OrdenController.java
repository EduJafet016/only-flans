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

    // Endpoint GET para que la WebApp del panel liste las órdenes pendientes
    @GetMapping
    public ResponseEntity<List<Orden>> obtenerOrdenesPendientes() {
        List<Orden> pendientes = ordenRepository.findByEstado(Orden.EstadoOrden.PENDIENTE);
        return ResponseEntity.ok(pendientes);
    }

    // Endpoint Webhook para MercadoPago
    @PostMapping("/webhook")
    public ResponseEntity<String> recibirWebhookMercadoPago(
            @RequestParam("type") String tipo,
            @RequestBody Map<String, Object> payload) {

        if ("payment".equals(tipo)) {
            Object rawData = payload.get("data");
            if (rawData instanceof Map<?, ?> rawMap) {
                Object idObj = rawMap.get("id");
                if (idObj != null) {
                    String paymentId = idObj.toString();
                    System.out.println("Webhook recibido con éxito para el pago ID: " + paymentId);
                }
            }
        }

        return ResponseEntity.ok("EVENT_RECEIVED");
    }
}