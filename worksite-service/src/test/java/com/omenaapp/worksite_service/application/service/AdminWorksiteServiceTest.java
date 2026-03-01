package com.omenaapp.worksite_service.application.service;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.omenaapp.worksite_service.domain.model.Assignment;
import com.omenaapp.worksite_service.domain.model.Worksite;
import com.omenaapp.worksite_service.domain.port.out.AssignmentRepositoryPort;
import com.omenaapp.worksite_service.domain.port.out.WorksiteRepositoryPort;

@ExtendWith(MockitoExtension.class)
class AdminWorksiteServiceTest {

    private WorksiteRepositoryPort worksites;
    private AssignmentRepositoryPort assignments;
    private AdminWorksiteService service;

    @BeforeEach
    void setUp() {
        worksites = mock(WorksiteRepositoryPort.class);
        assignments = mock(AssignmentRepositoryPort.class);
        service = new AdminWorksiteService(worksites, assignments);
    }

    @Test
    void listWorksites_delegatesToPort() {
        List<Worksite> expected = List.of(
                new Worksite(UUID.randomUUID(), "A", "addr1", java.time.Instant.now()),
                new Worksite(UUID.randomUUID(), "B", "addr2", java.time.Instant.now())
        );
        when(worksites.findAll()).thenReturn(expected);

        List<Worksite> result = service.listWorksites();

        assertSame(expected, result);
        verify(worksites).findAll();
        verifyNoMoreInteractions(worksites, assignments);
    }

    @Test
    void createWorksite_invalidName_throwsWithCode() {
        IllegalArgumentException ex1 =
                assertThrows(IllegalArgumentException.class, () -> service.createWorksite(null, "x"));
        assertEquals("INVALID_NAME", ex1.getMessage());

        IllegalArgumentException ex2 =
                assertThrows(IllegalArgumentException.class, () -> service.createWorksite("a", "x"));
        assertEquals("INVALID_NAME", ex2.getMessage());

        verifyNoInteractions(worksites, assignments);
    }

    @Test
    void createWorksite_trimsName_savesWorksite_andReturnsId() {
        UUID returnedId = service.createWorksite("  test2  ", "3 rue de la place");

        assertNotNull(returnedId);

        ArgumentCaptor<Worksite> captor = ArgumentCaptor.forClass(Worksite.class);
        verify(worksites).save(captor.capture());

        Worksite saved = captor.getValue();
        assertEquals(returnedId, saved.id());
        assertEquals("test2", saved.name());
        assertEquals("3 rue de la place", saved.address());
        assertNotNull(saved.createdAt());

        verifyNoMoreInteractions(worksites, assignments);
    }

    @Test
    void deleteWorksite_notFound_throws_andDoesNotDelete() {
        UUID id = UUID.randomUUID();
        when(worksites.existsById(id)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.deleteWorksite(id));
        assertEquals("WORKSITE_NOT_FOUND", ex.getMessage());

        verify(worksites).existsById(id);
        verify(worksites, never()).deleteById(any());
        verifyNoMoreInteractions(worksites);
        verifyNoInteractions(assignments);
    }

    @Test
    void deleteWorksite_found_deletes() {
        UUID id = UUID.randomUUID();
        when(worksites.existsById(id)).thenReturn(true);

        service.deleteWorksite(id);

        verify(worksites).existsById(id);
        verify(worksites).deleteById(id);
        verifyNoMoreInteractions(worksites);
        verifyNoInteractions(assignments);
    }

    @Test
    void assignUser_worksiteNotFound_throws_andDoesNotSaveAssignment() {
        UUID worksiteId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(worksites.existsById(worksiteId)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.assignUser(worksiteId, userId));
        assertEquals("WORKSITE_NOT_FOUND", ex.getMessage());

        verify(worksites).existsById(worksiteId);
        verifyNoInteractions(assignments);
        verifyNoMoreInteractions(worksites);
    }

    @Test
    void assignUser_worksiteExists_savesAssignment() {
        UUID worksiteId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(worksites.existsById(worksiteId)).thenReturn(true);

        service.assignUser(worksiteId, userId);

        ArgumentCaptor<Assignment> captor = ArgumentCaptor.forClass(Assignment.class);
        verify(assignments).save(captor.capture());

        Assignment saved = captor.getValue();
        assertNotNull(saved.id());
        assertEquals(worksiteId, saved.worksiteId());
        assertEquals(userId, saved.userId());
        assertNotNull(saved.assignedAt());

        verify(worksites).existsById(worksiteId);
        verifyNoMoreInteractions(worksites, assignments);
    }

    @Test
    void assignUser_duplicateOrDbError_isIgnored_bestEffort() {
        UUID worksiteId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(worksites.existsById(worksiteId)).thenReturn(true);

        doThrow(new RuntimeException("duplicate")).when(assignments).save(any());

        assertDoesNotThrow(() -> service.assignUser(worksiteId, userId));

        verify(worksites).existsById(worksiteId);
        verify(assignments).save(any());
        verifyNoMoreInteractions(worksites, assignments);
    }

    @Test
    void unassignUser_delegatesToPort() {
        UUID worksiteId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        service.unassignUser(worksiteId, userId);

        verify(assignments).deleteByWorksiteIdAndUserId(worksiteId, userId);
        verifyNoInteractions(worksites);
        verifyNoMoreInteractions(assignments);
    }

    @Test
    void listAssignments_delegatesToPort() {
        UUID worksiteId = UUID.randomUUID();
        List<Assignment> expected = List.of(
                new Assignment(UUID.randomUUID(), worksiteId, UUID.randomUUID(), java.time.Instant.now())
        );
        when(assignments.findByWorksiteId(worksiteId)).thenReturn(expected);

        List<Assignment> result = service.listAssignments(worksiteId);

        assertSame(expected, result);
        verify(assignments).findByWorksiteId(worksiteId);
        verifyNoInteractions(worksites);
        verifyNoMoreInteractions(assignments);
    }
}