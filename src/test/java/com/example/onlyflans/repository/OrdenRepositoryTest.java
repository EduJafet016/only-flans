package com.example.onlyflans.repository;

import com.example.onlyflans.model.Cliente;
import com.example.onlyflans.model.Lote;
import com.example.onlyflans.model.Orden;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

// Levantamos el contexto completo pero sobrescribimos las variables de Railway
// para aislar la prueba en memoria con H2 y evitar crashes por tokens faltantes.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
                "spring.datasource.driverClassName=org.h2.Driver",
                "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
                "spring.jpa.hibernate.ddl-auto=create-drop",
                "api.mercadopago.access-token=TEST_TOKEN_SIMULADO",
                "wa.token=TOKEN_SIMULADO_WHATSAPP",
                "wa.phone.number.id=123456789"
})
@Transactional // Garantiza el rollback automático después de la aserción
class OrdenRepositoryTest {

    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private LoteRepository loteRepository;

    @Test
    void debePersistirYRecuperarOrdenConRelaciones() {
        // 1. Arrange (Preparación)
        Cliente cliente = new Cliente("2281112233", "Carlos");
        clienteRepository.save(cliente);

        Lote lote = new Lote();
        lote.setFecha(LocalDate.now());
        lote.setHoraCorte(LocalTime.of(14, 0));
        loteRepository.save(lote);

        Orden orden = new Orden();
        orden.setCliente(cliente);
        orden.setLote(lote);
        orden.setEstado(Orden.EstadoOrden.PENDIENTE);
        orden.setMonto(new BigDecimal("300.00"));

        // 2. Act (Ejecución)
        Orden ordenGuardada = ordenRepository.save(orden);
        // Forzamos la limpieza del caché de primer nivel de Hibernate (opcional pero recomendado en tests duros)
        // ordenRepository.flush();
        Orden ordenRecuperada = ordenRepository.findById(ordenGuardada.getId()).orElse(null);

        // 3. Assert (Validación)
        assertNotNull(ordenRecuperada);
        assertEquals(Orden.EstadoOrden.PENDIENTE, ordenRecuperada.getEstado());
        assertEquals("Carlos", ordenRecuperada.getCliente().getNombre());
    }
}