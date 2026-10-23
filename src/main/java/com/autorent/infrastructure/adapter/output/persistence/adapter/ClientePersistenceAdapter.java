package com.autorent.infrastructure.adapter.output.persistence.adapter;

import com.autorent.application.port.output.ClienteRepositoryPort;
import com.autorent.application.port.output.TrazaPort;
import com.autorent.domain.model.Cliente;
import com.autorent.infrastructure.adapter.output.persistence.entity.ClienteEntity;
import com.autorent.infrastructure.adapter.output.persistence.repository.ClienteJpaRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class ClientePersistenceAdapter implements ClienteRepositoryPort {

    private final ClienteJpaRepository repository;
    private final TrazaPort traza;

    public ClientePersistenceAdapter(ClienteJpaRepository repository, TrazaPort traza) {
        this.repository = repository;
        this.traza = traza;
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        traza.registrar("node-port-out", "ClienteRepositoryPort.guardar");
        traza.registrar("node-adapter-out", "ClientePersistenceAdapter: guardar cliente en H2");
        ClienteEntity entity = new ClienteEntity();
        entity.setId(cliente.getId());
        entity.setNombre(cliente.getNombre());
        entity.setDocumento(cliente.getDocumento());
        entity.setTelefono(cliente.getTelefono());
        entity.setEmail(cliente.getEmail());
        
        ClienteEntity saved = repository.save(entity);
        
        return toDomain(saved);
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) {
        traza.registrar("node-port-out", "ClienteRepositoryPort.buscarPorId");
        traza.registrar("node-adapter-out", "ClientePersistenceAdapter: consultar cliente en H2");
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public boolean existePorDocumento(String documento) {
        traza.registrar("node-port-out", "ClienteRepositoryPort.existePorDocumento");
        traza.registrar("node-adapter-out", "ClientePersistenceAdapter: consultar documento en H2");
        return repository.existsByDocumento(documento);
    }

    private Cliente toDomain(ClienteEntity entity) {
        return new Cliente(entity.getId(), entity.getNombre(), entity.getDocumento(), 
                           entity.getTelefono(), entity.getEmail());
    }
}