package com.example.onlyflans.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WhatsAppService {

    @Value("${wa.token}")
    private String token;

    @Value("${wa.phone.number.id}")
    private String phoneNumberId; // ID del número de teléfono provisto por Meta

    private final RestTemplate restTemplate = new RestTemplate();

    public void enviarLinkDePago(String telefonoDestino, String linkPago) {
        String url = "https://graph.facebook.com/v17.0/" + phoneNumberId + "/messages";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        // Estructura del JSON que exige la Cloud API de Meta
        String cuerpoJson = """
            {
              "messaging_product": "whatsapp",
              "to": "%s",
              "type": "text",
              "text": {
                "body": "¡Hola! Gracias por tu pedido en Only Flans. 🍮 Puedes realizar tu pago de forma segura en el siguiente enlace: %s"
              }
            }
            """.formatted(telefonoDestino, linkPago);

        HttpEntity<String> entity = new HttpEntity<>(cuerpoJson, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Mensaje de WhatsApp enviado con éxito a: " + telefonoDestino);
            }
        } catch (Exception e) {
            System.err.println("Error al enviar mensaje por WhatsApp: " + e.getMessage());
        }
    }
}