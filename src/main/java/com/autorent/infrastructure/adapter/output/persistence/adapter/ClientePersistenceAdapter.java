package com.autorent.infrastructure.adapter.output.persistence.adapter;

import com.autorent.application.port.output.ClienteRepositoryPort;
import com.autorent.domain.model.Cliente;
import com.autorent.infrastructure.adapter.output.persistence.entity.ClienteEntity;
import com.autorent.infrastructure.adapter.output.persistence.repository.ClienteJpaRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class ClientePersistenceAdapter implements ClienteRepositoryPort {

    private final ClienteJpaRepository repository;

    public ClientePersistenceAdapter(ClienteJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Cliente guardar(Cliente cliente) {


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


        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Cliente> buscarPorDocumento(String documento) {
        return repository.findByDocumento(documento).map(this::toDomain);
    }

    @Override
    public Optional<Cliente> buscarPorEmail(String email) {
        return repository.findByEmailIgnoreCase(email).map(this::toDomain);
    }

    private Cliente toDomain(ClienteEntity entity) {
        return new Cliente(entity.getId(), entity.getNombre(), entity.getDocumento(), 
                           entity.getTelefono(), entity.getEmail());
    }
}