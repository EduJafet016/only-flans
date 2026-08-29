package com.example.onlyflans.service;

import com.example.onlyflans.model.Lote;
import com.example.onlyflans.repository.LoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GestorDeInventarioTest {

    @Mock
    private LoteRepository loteRepository;

    @InjectMocks
    private GestorDeInventario gestorDeInventario;

    private Lote loteMock;
    private LocalDate fechaPrueba;

    @BeforeEach
    void setUp() {
        fechaPrueba = LocalDate.of(2026, 8, 30);
        loteMock = new Lote();
        loteMock.setFecha(fechaPrueba);
        loteMock.setHoraCorte(LocalTime.of(14, 0));
        // Lote recién creado, 0 unidades reservadas de 3 posibles
    }

    @Test
    void debeAsignarLoteCuandoHayEspacioDisponible() {
        // Arrange: Configuramos el mock para que devuelva nuestro loteMock cuando el servicio consulte la base de datos
        when(loteRepository.findByFechaOrderByHoraCorteAsc(fechaPrueba))
                .thenReturn(Collections.singletonList(loteMock));
        when(loteRepository.save(any(Lote.class))).thenReturn(loteMock);

        // Act: Intentamos reservar 2 unidades (el límite físico es 3)
        Lote resultado = gestorDeInventario.asignarLoteDisponible(fechaPrueba, 2);

        // Assert: Validamos que la reserva se hizo en memoria y se llamó al método save
        assertNotNull(resultado);
        assertEquals(2, resultado.getUnidadesReservadas());
        verify(loteRepository, times(1)).save(loteMock);
    }

    @Test
    void debeLanzarExcepcionCuandoCapacidadEsExcedida() {
        // Arrange: Llenamos el lote artificialmente al límite
        loteMock.reservarUnidades(3);

        when(loteRepository.findByFechaOrderByHoraCorteAsc(fechaPrueba))
                .thenReturn(Collections.singletonList(loteMock));

        // Act & Assert: Intentar reservar 1 unidad extra en un lote lleno debe arrojar RuntimeException
        Exception excepcion = assertThrows(RuntimeException.class, () -> gestorDeInventario.asignarLoteDisponible(fechaPrueba, 1));

        assertTrue(excepcion.getMessage().contains("Sold out"));

        // Verificamos que el repositorio nunca intentó guardar un estado inconsistente en la base de datos
        verify(loteRepository, never()).save(any(Lote.class));
    }
}