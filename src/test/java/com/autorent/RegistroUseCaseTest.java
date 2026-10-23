package com.autorent;

import com.autorent.application.PasoTraza;
import com.autorent.application.port.output.*;
import com.autorent.application.port.input.*;
import com.autorent.application.usecase.*;
import com.autorent.domain.model.*;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class RegistroUseCaseTest {
    TrazaPort traza = new TrazaPort() {
        public void registrar(String nodo, String detalle) {}
        public List<PasoTraza> obtenerPasos() { return List.of(); }
    };

    @Test
    void autoDebeTenerPlacaYPrecioValidos() {
        AutoRepositoryPort repo = new AutoRepositoryPort() {
            public Auto guardar(Auto a) { a.setId(10L); return a; }
            public Optional<Auto> buscarPorId(Long id) { return Optional.empty(); }
            public List<Auto> buscarDisponibles() { return List.of(); }
            public List<Auto> buscarTodos() { return List.of(); }
        };
        AutoService servicio = new AutoUseCase(repo, traza);
        Auto auto = new Auto(null, "ABC123", "Toyota", "Corolla", 2025, 150000.0, null, null);
        assertEquals(Auto.EstadoAuto.DISPONIBLE, servicio.registrar(auto).getEstado());
        auto.setPlaca("");
        assertThrows(IllegalArgumentException.class, () -> servicio.registrar(auto));
    }

    @Test
    void clienteRepetidoSeRechaza() {
        ClienteRepositoryPort repo = new ClienteRepositoryPort() {
            public Cliente guardar(Cliente c) { return c; }
            public Optional<Cliente> buscarPorId(Long id) { return Optional.empty(); }
            public boolean existePorDocumento(String doc) { return "123".equals(doc); }
        };
        ClienteService servicio = new ClienteUseCase(repo, traza);
        assertThrows(IllegalStateException.class, () -> servicio.registrar(
                new Cliente(null, "Ana", "123", "300", "ana@email.com")));
        assertThrows(IllegalArgumentException.class, () -> servicio.registrar(
                new Cliente(null, "Ana", "999", "300", "no-es-email")));
    }
}
