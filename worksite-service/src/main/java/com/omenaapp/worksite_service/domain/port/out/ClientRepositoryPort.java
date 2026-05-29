package com.omenaapp.worksite_service.domain.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.omenaapp.worksite_service.domain.model.Client;

public interface ClientRepositoryPort {
    List<Client> findAll();
    Optional<Client> findById(UUID id);
    boolean existsById(UUID id);
    Client save(Client client);
    void deleteById(UUID id);
}