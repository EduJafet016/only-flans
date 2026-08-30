package com.example.onlyflans.controller;

import com.example.onlyflans.service.OrdenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {

    private final OrdenService ordenService;

    public WebhookController(OrdenService ordenService) {
        this.ordenService = ordenService;
    }

    @PostMapping("/mercadopago")
    public ResponseEntity<String> recibirNotificacionMercadoPago(@RequestBody Map<String, Object> payload) {
        String tipo = (String) payload.get("type");

        if ("payment".equals(tipo)) {
            Map<String, Object> data = (Map<String, Object>) payload.get("data");
            if (data != null && data.containsKey("id")) {
                String paymentId = data.get("id").toString();
                ordenService.procesarNotificacionPago(paymentId);
            }
        }

        return ResponseEntity.ok("OK");
    }
}