package com.omenaapp.worksite_service.application.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.omenaapp.worksite_service.domain.model.Client;
import com.omenaapp.worksite_service.domain.port.out.ClientRepositoryPort;

@Service
public class ClientService {
    
    private final ClientRepositoryPort clientRepository;
    
    public ClientService(ClientRepositoryPort clientRepository) {
        this.clientRepository = clientRepository;
    }
    
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }
    
    public Optional<Client> getClientById(UUID id) {
        return clientRepository.findById(id);
    }
    
    public Client createClient(Client client) {
        return clientRepository.save(client);
    }
    
    public void deleteClient(UUID id) {
        clientRepository.deleteById(id);
    }
}