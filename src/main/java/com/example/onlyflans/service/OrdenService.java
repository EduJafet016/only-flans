package com.example.onlyflans.service;

import com.example.onlyflans.dto.OrdenRequest;
import com.example.onlyflans.model.Cliente;
import com.example.onlyflans.model.Lote;
import com.example.onlyflans.model.Orden;
import com.example.onlyflans.repository.ClienteRepository;
import com.example.onlyflans.repository.OrdenRepository;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.resources.payment.Payment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class OrdenService {

    private final ClienteRepository clienteRepository;
    private final OrdenRepository ordenRepository;
    private final GestorDeInventario gestorDeInventario;
    private final MercadoPagoService mercadoPagoService;
    private final WhatsAppService whatsAppService;

    public OrdenService(ClienteRepository clienteRepository,
                        OrdenRepository ordenRepository,
                        GestorDeInventario gestorDeInventario,
                        MercadoPagoService mercadoPagoService,
                        WhatsAppService whatsAppService) {
        this.clienteRepository = clienteRepository;
        this.ordenRepository = ordenRepository;
        this.gestorDeInventario = gestorDeInventario;
        this.mercadoPagoService = mercadoPagoService;
        this.whatsAppService = whatsAppService;
    }

    @Transactional
    public String procesarNuevaOrden(OrdenRequest request) {
        Cliente cliente = clienteRepository.findByTelefono(request.getTelefono())
                .orElseGet(() -> clienteRepository.save(new Cliente(request.getTelefono(), request.getNombre())));

        Lote loteAsignado = gestorDeInventario.asignarLoteDisponible(request.getFechaDeseada(), request.getCantidad());

        Orden orden = new Orden();
        orden.setCliente(cliente);
        orden.setLote(loteAsignado);
        orden.setEstado(Orden.EstadoOrden.PENDIENTE);

        // Mapeo de la persistencia de datos faltantes para la base de datos
        orden.setCantidad(request.getCantidad());
        orden.setFechaDeseada(request.getFechaDeseada());

        // Ajuste del unitPrice a 250.00 para la preferencia de Mercado Pago y registro interno
        orden.setMonto(new BigDecimal("250.00").multiply(new BigDecimal(request.getCantidad())));

        ordenRepository.save(orden);

        String urlPago = mercadoPagoService.generarLinkDePago(orden);
        whatsAppService.enviarLinkDePago(request.getTelefono(), urlPago);

        return urlPago;
    }

    @Transactional
    public void procesarNotificacionPago(String paymentId) {
        try {
            PaymentClient client = new PaymentClient();
            Payment mpPayment = client.get(Long.parseLong(paymentId));

            if ("approved".equals(mpPayment.getStatus())) {
                String externalRef = mpPayment.getExternalReference();
                if (externalRef != null) {
                    Long ordenId = Long.parseLong(externalRef);

                    Orden orden = ordenRepository.findById(ordenId).orElse(null);
                    if (orden != null && orden.getEstado() != Orden.EstadoOrden.PAGADO) {
                        orden.setEstado(Orden.EstadoOrden.PAGADO);
                        ordenRepository.save(orden);

                        String mensaje = "¡Pago confirmado! 🍮 Tu orden en Only Flans está asegurada en el lote correspondiente.";
                        whatsAppService.enviarLinkDePago(orden.getCliente().getTelefono(), mensaje);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error procesando webhook de pago: " + e.getMessage());
        }
    }
}