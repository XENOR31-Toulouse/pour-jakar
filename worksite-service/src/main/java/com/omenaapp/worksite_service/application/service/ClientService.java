package com.omenaapp.worksite_service.application.service;

import java.util.List;
import java.util.UUID;

import com.omenaapp.worksite_service.domain.model.Client;
import com.omenaapp.worksite_service.domain.port.out.ClientRepositoryPort;

/**
 * Application service (use-cases) for client operations.
 * No Spring annotations here: wired in infrastructure config.
 */
public class ClientService {

    private final ClientRepositoryPort clients;

    public ClientService(ClientRepositoryPort clients) {
        this.clients = clients;
    }

    public List<Client> listClients() {
        return clients.findAll();
    }

    public Client getClient(UUID id) {
        return clients.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("CLIENT_NOT_FOUND"));
    }

    public UUID createClient(String name, String email, String phoneNumber, String address) {
        return new AdminWorksiteService(null, null, clients).createClient(name, email, phoneNumber, address);
    }
}