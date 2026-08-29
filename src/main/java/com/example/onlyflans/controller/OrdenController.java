package com.example.onlyflans.controller;

import com.example.onlyflans.dto.OrdenRequest;
import com.example.onlyflans.service.OrdenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ordenes")
public class OrdenController {

    private final OrdenService ordenService;

    public OrdenController(OrdenService ordenService) {
        this.ordenService = ordenService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<String> iniciarCheckout(@RequestBody OrdenRequest request) {
        try {
            String urlPago = ordenService.procesarNuevaOrden(request);
            return ResponseEntity.ok(urlPago);
        } catch (RuntimeException e) {
            // Retorna 409 Conflict si el Lote está lleno o falla la concurrencia
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }
}