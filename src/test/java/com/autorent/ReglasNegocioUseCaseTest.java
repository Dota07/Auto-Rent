package com.autorent;

import com.autorent.application.port.input.AutoService;
import com.autorent.application.port.input.ClienteService;
import com.autorent.application.port.output.*;
import com.autorent.application.usecase.*;
import com.autorent.domain.exception.*;
import com.autorent.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

// Datos únicos, edición y cambio manual de estado. Sin servidor ni H2: almacenamiento en memoria.
class ReglasNegocioUseCaseTest {
    private AutosMemoria autos;
    private ClientesMemoria clientes;
    private AlquileresMemoria alquileres;
    private AutoService autoService;
    private ClienteService clienteService;
    private AlquilerUseCase alquilerService;

    @BeforeEach
    void preparar() {
        autos = new AutosMemoria();
        clientes = new ClientesMemoria();
        alquileres = new AlquileresMemoria();
        autoService = new AutoUseCase(autos);
        clienteService = new ClienteUseCase(clientes, alquileres);
        alquilerService = new AlquilerUseCase(alquileres, autos, clientes);
    }

    private Auto nuevoAuto(String placa) {
        return autoService.registrar(new Auto(null, placa, "Toyota", "Corolla", 2025, 150000.0, null, null));
    }

    private Cliente nuevoCliente(String doc, String email) {
        return clienteService.registrar(new Cliente(null, "Carlos", doc, "300", email));
    }

    // ---- Datos únicos ----
    @Test
    void placaRepetidaSeRechazaAlRegistrar() {
        nuevoAuto("ABC123");
        assertThrows(IllegalStateException.class, () -> nuevoAuto("ABC123"));
        assertThrows(IllegalStateException.class, () -> nuevoAuto("abc123 "));
    }

    @Test
    void documentoYEmailRepetidosSeRechazanAlRegistrar() {
        nuevoCliente("111", "a@email.com");
        assertThrows(IllegalStateException.class, () -> nuevoCliente("111", "otro@email.com"));
        assertThrows(IllegalStateException.class, () -> nuevoCliente("222", "A@email.com"));
    }

    @Test
    void alEditarNoSePuedeUsarLaPlacaDeOtroAutoPeroSiLaPropia() {
        Auto a = nuevoAuto("AAA111");
        Auto b = nuevoAuto("BBB222");
        Auto datos = new Auto(null, "AAA111", "Kia", "Rio", 2024, 100000.0, null, null);
        assertEquals("Kia", autoService.actualizar(a.getId(), datos).getMarca()); // su propia placa: OK
        Auto conPlacaAjena = new Auto(null, "BBB222", "Kia", "Rio", 2024, 100000.0, null, null);
        assertThrows(IllegalStateException.class, () -> autoService.actualizar(a.getId(), conPlacaAjena));
        assertEquals("BBB222", autos.datos.get(b.getId()).getPlaca());
    }

    @Test
    void alEditarNoSePuedeUsarDocumentoNiEmailDeOtroCliente() {
        Cliente a = nuevoCliente("111", "a@email.com");
        nuevoCliente("222", "b@email.com");
        assertThrows(IllegalStateException.class,
                () -> clienteService.actualizar(a.getId(), new Cliente(null, "X", "222", "3", "a@email.com")));
        assertThrows(IllegalStateException.class,
                () -> clienteService.actualizar(a.getId(), new Cliente(null, "X", "111", "3", "b@email.com")));
        Cliente ok = clienteService.actualizar(a.getId(), new Cliente(null, "Nuevo", "111", "3", "a@email.com"));
        assertEquals("Nuevo", ok.getNombre());
    }

    // ---- Edición de autos ----
    @Test
    void noSePuedeEditarUnAutoAlquiladoYLaEdicionNoCambiaElEstado() {
        Auto auto = nuevoAuto("AAA111");
        Cliente c = nuevoCliente("111", "a@email.com");
        Auto datos = new Auto(null, "AAA111", "Kia", "Rio", 2024, 100000.0, null, Auto.EstadoAuto.MANTENIMIENTO);
        Auto editado = autoService.actualizar(auto.getId(), datos);
        assertEquals(Auto.EstadoAuto.DISPONIBLE, editado.getEstado()); // ignora el estado enviado

        alquilerService.crear(c.getId(), auto.getId(), 2);
        assertThrows(IllegalStateException.class, () -> autoService.actualizar(auto.getId(), datos));
        assertThrows(AutoNoEncontradoException.class, () -> autoService.actualizar(999L, datos));
    }

    // ---- Cambio manual de estado ----
    @Test
    void estadoManualSoloDisponibleYMantenimiento() {
        Auto auto = nuevoAuto("AAA111");
        assertEquals(Auto.EstadoAuto.MANTENIMIENTO,
                autoService.cambiarEstado(auto.getId(), Auto.EstadoAuto.MANTENIMIENTO).getEstado());
        assertEquals(Auto.EstadoAuto.DISPONIBLE,
                autoService.cambiarEstado(auto.getId(), Auto.EstadoAuto.DISPONIBLE).getEstado());
        assertThrows(IllegalStateException.class,
                () -> autoService.cambiarEstado(auto.getId(), Auto.EstadoAuto.ALQUILADO));
        assertThrows(IllegalArgumentException.class, () -> autoService.cambiarEstado(auto.getId(), null));
        assertThrows(AutoNoEncontradoException.class,
                () -> autoService.cambiarEstado(999L, Auto.EstadoAuto.MANTENIMIENTO));
    }

    @Test
    void autoAlquiladoSoloCambiaAlFinalizar() {
        Auto auto = nuevoAuto("AAA111");
        Cliente c = nuevoCliente("111", "a@email.com");
        Alquiler alquiler = alquilerService.crear(c.getId(), auto.getId(), 2);
        assertThrows(IllegalStateException.class,
                () -> autoService.cambiarEstado(auto.getId(), Auto.EstadoAuto.DISPONIBLE));
        assertThrows(IllegalStateException.class,
                () -> autoService.cambiarEstado(auto.getId(), Auto.EstadoAuto.MANTENIMIENTO));
        alquilerService.finalizar(alquiler.getId());
        assertEquals(Auto.EstadoAuto.DISPONIBLE, autos.datos.get(auto.getId()).getEstado());
    }

    // ---- Edición de clientes ----
    @Test
    void noSePuedeEditarUnClienteConAlquilerActivo() {
        Auto auto = nuevoAuto("AAA111");
        Cliente c = nuevoCliente("111", "a@email.com");
        Alquiler alquiler = alquilerService.crear(c.getId(), auto.getId(), 2);
        Cliente datos = new Cliente(null, "Otro nombre", "111", "3", "a@email.com");
        assertThrows(IllegalStateException.class, () -> clienteService.actualizar(c.getId(), datos));

        alquilerService.finalizar(alquiler.getId()); // con el alquiler FINALIZADO ya se puede editar
        assertEquals("Otro nombre", clienteService.actualizar(c.getId(), datos).getNombre());
        assertThrows(ClienteNoEncontradoException.class, () -> clienteService.actualizar(999L, datos));
    }

    // ---- Almacenamiento en memoria ----
    private static class AutosMemoria implements AutoRepositoryPort {
        final Map<Long, Auto> datos = new HashMap<>();
        public Auto guardar(Auto a) {
            if (a.getId() == null) a.setId((long) (datos.size() + 1));
            datos.put(a.getId(), a);
            return a;
        }
        public Optional<Auto> buscarPorId(Long id) { return Optional.ofNullable(datos.get(id)); }
        public Optional<Auto> buscarPorPlaca(String p) {
            return datos.values().stream().filter(a -> a.getPlaca().equalsIgnoreCase(p)).findFirst();
        }
        public List<Auto> buscarDisponibles() {
            return datos.values().stream().filter(a -> a.getEstado() == Auto.EstadoAuto.DISPONIBLE).toList();
        }
        public List<Auto> buscarTodos() { return new ArrayList<>(datos.values()); }
    }

    private static class ClientesMemoria implements ClienteRepositoryPort {
        final Map<Long, Cliente> datos = new HashMap<>();
        public Cliente guardar(Cliente c) {
            if (c.getId() == null) c.setId((long) (datos.size() + 1));
            datos.put(c.getId(), c);
            return c;
        }
        public Optional<Cliente> buscarPorId(Long id) { return Optional.ofNullable(datos.get(id)); }
        public Optional<Cliente> buscarPorDocumento(String d) {
            return datos.values().stream().filter(c -> c.getDocumento().equals(d)).findFirst();
        }
        public Optional<Cliente> buscarPorEmail(String e) {
            return datos.values().stream().filter(c -> c.getEmail().equalsIgnoreCase(e)).findFirst();
        }
    }

    private static class AlquileresMemoria implements AlquilerRepositoryPort {
        final Map<Long, Alquiler> datos = new HashMap<>();
        public Alquiler guardar(Alquiler a) {
            if (a.getId() == null) a.setId((long) (datos.size() + 1));
            datos.put(a.getId(), a);
            return a;
        }
        public Optional<Alquiler> buscarPorId(Long id) { return Optional.ofNullable(datos.get(id)); }
        public boolean existeActivoPorClienteId(Long clienteId) {
            return datos.values().stream().anyMatch(a -> clienteId.equals(a.getClienteId())
                    && a.getEstado() == Alquiler.EstadoAlquiler.ACTIVO);
        }
    }
}
