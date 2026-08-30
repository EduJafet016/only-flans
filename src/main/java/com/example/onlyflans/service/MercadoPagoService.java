package com.example.onlyflans.service;

import com.example.onlyflans.model.Orden;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.preference.Preference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;

@Service
public class MercadoPagoService {

    @Value("${api.mercadopago.access-token}")
    private String accessToken;

    public String generarLinkDePago(Orden orden) {
        try {
            // Inicializar las credenciales de Mercado Pago
            MercadoPagoConfig.setAccessToken(accessToken);

            // Configurar el ítem que se está comprando
            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                    .title("Flan artesanal - Only Flans")
                    .quantity(1)
                    .unitPrice(orden.getMonto() != null ? orden.getMonto() : new BigDecimal("150.00"))
                    .currencyId("MXN")
                    .build();

            // Construir la solicitud de preferencia de pago
            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(Collections.singletonList(itemRequest))
                    .externalReference(orden.getId().toString()) // Vincula la orden interna
                    .notificationUrl("https://only-flans-production.up.railway.app/api/webhooks/mercadopago") // URL del Webhook en Railway
                    .build();

            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

            // Retorna el link de pago oficial generado por la pasarela
            return preference.getInitPoint();

        } catch (Exception e) {
            throw new RuntimeException("Error al conectar con la pasarela de Mercado Pago: " + e.getMessage(), e);
        }
    }
}