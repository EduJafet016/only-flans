package com.example.onlyflans.service;

import com.example.onlyflans.dto.OrdenRequest;
import com.example.onlyflans.model.Cliente;
import com.example.onlyflans.model.Lote;
import com.example.onlyflans.model.Orden;
import com.example.onlyflans.repository.ClienteRepository;
import com.example.onlyflans.repository.OrdenRepository;
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
        // 1. Resolver Cliente
        Cliente cliente = clienteRepository.findByTelefono(request.getTelefono())
                .orElseGet(() -> clienteRepository.save(new Cliente(request.getTelefono(), request.getNombre())));

        // 2. Control de Inventario (Recibe el String con la modalidad o bloque seleccionado)
        Lote loteAsignado = gestorDeInventario.asignarLoteDisponible(request.getFechaDeseada(), request.getCantidad());

        // 3. Crear y Persistir la Orden
        Orden orden = new Orden();
        orden.setCliente(cliente);
        orden.setLote(loteAsignado);
        orden.setEstado(Orden.EstadoOrden.PENDIENTE);
        // Precio base para la v1 del MVP
        orden.setMonto(new BigDecimal("150.00").multiply(new BigDecimal(request.getCantidad())));

        ordenRepository.save(orden);

        // 4. Orquestación Externa (Generar URL de pago y notificar al cliente)
        String urlPago = mercadoPagoService.generarLinkDePago(orden);
        whatsAppService.enviarLinkDePago(request.getTelefono(), urlPago);

        return urlPago;
    }
}