package com.autorent;

import com.autorent.application.port.output.*;
import com.autorent.application.port.input.*;
import com.autorent.application.usecase.*;
import com.autorent.domain.model.*;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class RegistroUseCaseTest {
    @Test
    void autoDebeTenerPlacaYPrecioValidos() {
        AutoRepositoryPort repo = new AutoRepositoryPort() {
            public Auto guardar(Auto a) { a.setId(10L); return a; }
            public Optional<Auto> buscarPorId(Long id) { return Optional.empty(); }
            public Optional<Auto> buscarPorPlaca(String placa) { return Optional.empty(); }
            public List<Auto> buscarDisponibles() { return List.of(); }
            public List<Auto> buscarTodos() { return List.of(); }
        };
        AutoService servicio = new AutoUseCase(repo);
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
            public Optional<Cliente> buscarPorDocumento(String doc) {
                return "123".equals(doc) ? Optional.of(new Cliente(1L, "X", "123", "3", "x@x.com")) : Optional.empty();
            }
            public Optional<Cliente> buscarPorEmail(String email) { return Optional.empty(); }
        };
        AlquilerRepositoryPort alquileres = new AlquilerRepositoryPort() {
            public Alquiler guardar(Alquiler a) { return a; }
            public Optional<Alquiler> buscarPorId(Long id) { return Optional.empty(); }
            public boolean existeActivoPorClienteId(Long clienteId) { return false; }
        };
        ClienteService servicio = new ClienteUseCase(repo, alquileres);
        assertThrows(IllegalStateException.class, () -> servicio.registrar(
                new Cliente(null, "Ana", "123", "300", "ana@email.com")));
        assertThrows(IllegalArgumentException.class, () -> servicio.registrar(
                new Cliente(null, "Ana", "999", "300", "no-es-email")));
    }
}
