package com.example.onlyflans.service;

import com.example.onlyflans.model.Orden;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MercadoPagoService {

    // Inyecta el token configurado en Railway
    @Value("${api.mercadopago.access-token}")
    private String accessToken;

    public String generarLinkDePago(Orden orden) {
        return "https://www.mercadopago.com.mx/checkout/v1/redirect?pref_id=SIMULADO_" + orden.getId();
    }
}