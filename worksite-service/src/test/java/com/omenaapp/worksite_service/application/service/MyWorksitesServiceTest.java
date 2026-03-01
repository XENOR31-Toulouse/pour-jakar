package com.omenaapp.worksite_service.application.service;

import com.omenaapp.worksite_service.domain.model.Assignment;
import com.omenaapp.worksite_service.domain.model.Worksite;
import com.omenaapp.worksite_service.domain.port.out.AssignmentRepositoryPort;
import com.omenaapp.worksite_service.domain.port.out.WorksiteRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MyWorksitesServiceTest {

    private AssignmentRepositoryPort assignments;
    private WorksiteRepositoryPort worksites;
    private MyWorksitesService service;

    @BeforeEach
    void setUp() {
        assignments = mock(AssignmentRepositoryPort.class);
        worksites = mock(WorksiteRepositoryPort.class);
        service = new MyWorksitesService(assignments, worksites);
    }

    @Test
    void listMyWorksites_mapsAssignmentsToWorksites_andFiltersMissing() {
        UUID userId = UUID.randomUUID();
        UUID ws1 = UUID.randomUUID();
        UUID ws2 = UUID.randomUUID();
        UUID wsMissing = UUID.randomUUID();

        when(assignments.findByUserId(userId)).thenReturn(List.of(
                new Assignment(UUID.randomUUID(), ws1, userId, Instant.now()),
                new Assignment(UUID.randomUUID(), wsMissing, userId, Instant.now()),
                new Assignment(UUID.randomUUID(), ws2, userId, Instant.now())
        ));

        Worksite w1 = new Worksite(ws1, "A", "addr1", Instant.now());
        Worksite w2 = new Worksite(ws2, "B", "addr2", Instant.now());

        when(worksites.findById(ws1)).thenReturn(Optional.of(w1));
        when(worksites.findById(wsMissing)).thenReturn(Optional.empty());
        when(worksites.findById(ws2)).thenReturn(Optional.of(w2));

        List<Worksite> result = service.listMyWorksites(userId);

        assertEquals(List.of(w1, w2), result);
        verify(assignments).findByUserId(userId);
        verify(worksites).findById(ws1);
        verify(worksites).findById(wsMissing);
        verify(worksites).findById(ws2);
        verifyNoMoreInteractions(assignments, worksites);
    }
}
