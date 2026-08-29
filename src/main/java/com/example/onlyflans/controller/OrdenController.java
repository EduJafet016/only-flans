package com.example.onlyflans.controller;

import com.example.onlyflans.dto.OrdenRequest;
import com.example.onlyflans.service.OrdenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController

@GetMapping
public ResponseEntity<List<Orden>> obtenerOrdenesPendientes() {
    // Retorna las órdenes con estado PENDIENTE para que la cocina las vea
    List<Orden> pendientes = ordenRepository.findByEstado(Orden.EstadoOrden.PENDIENTE);
    return ResponseEntity.ok(pendientes);
}

@RequestMapping("/api/ordenes")
public class OrdenController {

    private final OrdenService ordenService;

    // Inyección de dependencias a través del constructor
    public OrdenController(OrdenService ordenService) {
        this.ordenService = ordenService;
    }

    // Endpoint para procesar la nueva orden, descontar inventario y generar pago
    @PostMapping
    public ResponseEntity<String> crearOrden(@RequestBody OrdenRequest request) {
        String urlPago = ordenService.procesarNuevaOrden(request);
        return ResponseEntity.ok(urlPago);
    }

    // Endpoint Webhook para escuchar las notificaciones de pago de MercadoPago
    @PostMapping("/webhook")
    public ResponseEntity<String> recibirWebhookMercadoPago(
            @RequestParam("type") String tipo,
            @RequestBody Map<String, Object> payload) {

        if ("payment".equals(tipo)) {
            Object rawData = payload.get("data");

            // Verificamos de forma segura que 'rawData' sea un mapa antes de castearlo
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