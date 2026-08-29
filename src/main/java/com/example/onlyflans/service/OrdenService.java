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

    public OrdenService(ClienteRepository clienteRepository, OrdenRepository ordenRepository,
                        GestorDeInventario gestorDeInventario, MercadoPagoService mercadoPagoService) {
        this.clienteRepository = clienteRepository;
        this.ordenRepository = ordenRepository;
        this.gestorDeInventario = gestorDeInventario;
        this.mercadoPagoService = mercadoPagoService;
    }

    @Transactional
    public String procesarNuevaOrden(OrdenRequest request) {
        // 1. Resolver Cliente
        Cliente cliente = clienteRepository.findByTelefono(request.getTelefono())
                .orElseGet(() -> clienteRepository.save(new Cliente(request.getTelefono(), request.getNombre())));

        // 2. Control de Inventario (Bloqueo Optimista en acción)
        Lote loteAsignado = gestorDeInventario.asignarLoteDisponible(request.getFechaDeseada(), request.getCantidad());

        // 3. Crear Orden
        Orden orden = new Orden();
        orden.setCliente(cliente);
        orden.setLote(loteAsignado);
        orden.setEstado(Orden.EstadoOrden.PENDIENTE);
        // Precio hardcodeado para la v1 del MVP
        orden.setMonto(new BigDecimal("150.00").multiply(new BigDecimal(request.getCantidad())));

        ordenRepository.save(orden);

        // 4. Generar URL de pago
        return mercadoPagoService.generarLinkDePago(orden);
    }
}