package com.omenaapp.worksite_service.application.service;

import com.omenaapp.worksite_service.domain.model.Client;
import com.omenaapp.worksite_service.domain.port.out.ClientRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    private ClientRepositoryPort clients;
    private ClientService service;

    @BeforeEach
    void setUp() {
        clients = mock(ClientRepositoryPort.class);
        service = new ClientService(clients);
    }

    @Test
    void listClients_delegatesToPort() {
        List<Client> expected = List.of(
                new Client(UUID.randomUUID(), "Client A", "clientA@example.com", "123456789", "Address 1"),
                new Client(UUID.randomUUID(), "Client B", "clientB@example.com", "987654321", "Address 2")
        );
        when(clients.findAll()).thenReturn(expected);

        List<Client> result = service.listClients();

        assertSame(expected, result);
        verify(clients).findAll();
        verifyNoMoreInteractions(clients);
    }

    @Test
    void getClient_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(clients.findById(id)).thenReturn(java.util.Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.getClient(id));
        assertEquals("CLIENT_NOT_FOUND", ex.getMessage());

        verify(clients).findById(id);
        verifyNoMoreInteractions(clients);
    }

    @Test
    void getClient_found_returnsClient() {
        UUID id = UUID.randomUUID();
        Client expected = new Client(id, "Client A", "clientA@example.com", "123456789", "Address 1");
        when(clients.findById(id)).thenReturn(java.util.Optional.of(expected));

        Client result = service.getClient(id);

        assertSame(expected, result);
        verify(clients).findById(id);
        verifyNoMoreInteractions(clients);
    }

    @Test
    void createClient_validData_savesAndReturnsId() {
        String name = "Client A";
        String email = "clientA@example.com";
        String phoneNumber = "123456789";
        String address = "Address 1";
        
        UUID returnedId = service.createClient(name, email, phoneNumber, address);

        assertNotNull(returnedId);

        ArgumentCaptor<Client> captor = ArgumentCaptor.forClass(Client.class);
        verify(clients).save(captor.capture());

        Client saved = captor.getValue();
        assertEquals(name, saved.name());
        assertEquals(email, saved.email());
        assertEquals(phoneNumber, saved.phoneNumber());
        assertEquals(address, saved.address());
        assertNotNull(saved.id());

        verifyNoMoreInteractions(clients);
    }
}