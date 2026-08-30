package com.example.onlyflans.service;

import com.example.onlyflans.model.Lote;
import com.example.onlyflans.repository.LoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class GestorDeInventarioTestTest {

    @Mock
    private LoteRepository loteRepository;

    @InjectMocks
    private GestorDeInventario gestorDeInventario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCantidadInvalida() {
        // Validación de cantidad menor o igual a cero
        assertThrows(IllegalArgumentException.class, () -> {
            gestorDeInventario.asignarLoteDisponible("INMEDIATA", 0);
        });
    }

    @Test
    void testAsignarStockInmediato() {
        when(loteRepository.findAll()).thenReturn(List.of(new Lote()));

        // CORREGIDO: Se pasa un String ("INMEDIATA") en lugar de LocalDate.now()
        Lote resultado = gestorDeInventario.asignarLoteDisponible("INMEDIATA", 1);

        assertNotNull(resultado);
    }

    @Test
    void testAsignarBloqueHorneado() {
        when(loteRepository.findAll()).thenReturn(List.of(new Lote()));

        // CORREGIDO: Se pasa un String con el bloque de horario ("HOY-10:00") en lugar de LocalDate.now()
        Lote resultado = gestorDeInventario.asignarLoteDisponible("HOY-10:00", 2);

        assertNotNull(resultado);
    }
}