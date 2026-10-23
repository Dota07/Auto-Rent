package com.autorent;

import com.autorent.application.PasoTraza;
import com.autorent.application.port.output.*;
import com.autorent.application.port.input.AlquilerService;
import com.autorent.application.usecase.AlquilerUseCase;
import com.autorent.domain.exception.*;
import com.autorent.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

// Pruebas sin servidor ni H2: se simula solo el almacenamiento, NO la API.
class AlquilerUseCaseTest {
    private AlquilerService servicio;
    private AutosMemoria autos;
    private AlquileresMemoria alquileres;

    @BeforeEach
    void preparar() {
        autos = new AutosMemoria();
        alquileres = new AlquileresMemoria();
        Auto auto = new Auto();
        auto.setId(1L);
        auto.setPrecioPorDia(150000.0);
        auto.setEstado(Auto.EstadoAuto.DISPONIBLE);
        autos.datos.put(1L, auto);
        Cliente cliente = new Cliente(1L, "Carlos", "123", "300123", "carlos@email.com");
        ClienteRepositoryPort clientes = new ClienteRepositoryPort() {
            public Cliente guardar(Cliente c) { return c; }
            public Optional<Cliente> buscarPorId(Long id) {
                return id.equals(1L) ? Optional.of(cliente) : Optional.empty();
            }
            public boolean existePorDocumento(String doc) { return doc.equals("123"); }
        };
        TrazaPort traza = new TrazaPort() {
            private final List<PasoTraza> pasos = new ArrayList<>();
            public void registrar(String nodo, String detalle) { pasos.add(new PasoTraza(nodo, detalle)); }
            public List<PasoTraza> obtenerPasos() { return pasos; }
        };
        servicio = new AlquilerUseCase(alquileres, autos, clientes, traza);
    }

    @Test
    void creaAlquilerYCalculaElTotal() {
        Alquiler alquiler = servicio.crear(1L, 1L, 5);
        assertEquals(750000.0, alquiler.getValorTotal(), 0.001);
        assertEquals(Alquiler.EstadoAlquiler.ACTIVO, alquiler.getEstado());
        assertEquals(Auto.EstadoAuto.ALQUILADO, autos.datos.get(1L).getEstado());
    }

    @Test
    void noPermiteAlquilarAutoOcupado() {
        servicio.crear(1L, 1L, 2);
        assertThrows(AutoNoDisponibleException.class, () -> servicio.crear(1L, 1L, 3));
    }

    @Test
    void validaClienteYDias() {
        assertThrows(ClienteNoEncontradoException.class, () -> servicio.crear(99L, 1L, 2));
        assertThrows(IllegalArgumentException.class, () -> servicio.crear(1L, 1L, 0));
        assertThrows(IllegalArgumentException.class, () -> servicio.crear(1L, 1L, null));
    }

    @Test
    void finalizarDevuelveElAutoADisponibleYNoPermiteRepetir() {
        Alquiler alquiler = servicio.crear(1L, 1L, 2);
        Alquiler finalizado = servicio.finalizar(alquiler.getId());
        assertEquals(Alquiler.EstadoAlquiler.FINALIZADO, finalizado.getEstado());
        assertEquals(Auto.EstadoAuto.DISPONIBLE, autos.datos.get(1L).getEstado());
        assertThrows(IllegalStateException.class, () -> servicio.finalizar(alquiler.getId()));
    }

    @Test
    void noPermiteFinalizarUnAlquilerInexistente() {
        assertThrows(AlquilerNoEncontradoException.class, () -> servicio.finalizar(999L));
    }

    private static class AutosMemoria implements AutoRepositoryPort {
        final Map<Long, Auto> datos = new HashMap<>();
        public Auto guardar(Auto auto) { datos.put(auto.getId(), auto); return auto; }
        public Optional<Auto> buscarPorId(Long id) { return Optional.ofNullable(datos.get(id)); }
        public List<Auto> buscarDisponibles() { return datos.values().stream().filter(a -> a.getEstado() == Auto.EstadoAuto.DISPONIBLE).toList(); }
        public List<Auto> buscarTodos() { return new ArrayList<>(datos.values()); }
    }

    private static class AlquileresMemoria implements AlquilerRepositoryPort {
        final Map<Long, Alquiler> datos = new HashMap<>();
        public Alquiler guardar(Alquiler alquiler) {
            if (alquiler.getId() == null) alquiler.setId((long) (datos.size() + 1));
            datos.put(alquiler.getId(), alquiler);
            return alquiler;
        }
        public Optional<Alquiler> buscarPorId(Long id) { return Optional.ofNullable(datos.get(id)); }
    }
}
