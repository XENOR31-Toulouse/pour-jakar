package com.omenaapp.worksite_service.adapter.out.persistence.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.omenaapp.worksite_service.adapter.out.persistence.entity.ClientEntity;
import com.omenaapp.worksite_service.adapter.out.persistence.repository.ClientRepo;
import com.omenaapp.worksite_service.domain.model.Client;
import com.omenaapp.worksite_service.domain.port.out.ClientRepositoryPort;

@Component
public class ClientPersistenceAdapter implements ClientRepositoryPort {
    
    private final ClientRepo repo;
    
    public ClientPersistenceAdapter(ClientRepo repo) {
        this.repo = repo;
    }
    
    @Override
    public List<Client> findAll() {
        return repo.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<Client> findById(UUID id) {
        return repo.findById(id).map(this::toDomain);
    }
    
    @Override
    public boolean existsById(UUID id) {
        return repo.existsById(id);
    }
    
    @Override
    public Client save(Client client) {
        ClientEntity entity = toEntity(client);
        ClientEntity savedEntity = repo.save(entity);
        return toDomain(savedEntity);
    }
    
    @Override
    public void deleteById(UUID id) {
        repo.deleteById(id);
    }
    
    private Client toDomain(ClientEntity entity) {
        return new Client(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getPhoneNumber(),
                entity.getAddress(),
                entity.getCreatedAt()
        );
    }
    
    private ClientEntity toEntity(Client client) {
        return new ClientEntity(
                client.id(),
                client.name(),
                client.email(),
                client.phoneNumber(),
                client.address(),
                client.createdAt()
        );
    }
}