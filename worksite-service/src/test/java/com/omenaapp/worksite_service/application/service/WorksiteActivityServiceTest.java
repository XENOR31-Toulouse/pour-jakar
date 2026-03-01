package com.omenaapp.worksite_service.application.service;

import com.omenaapp.worksite_service.domain.model.ProgressUpdate;
import com.omenaapp.worksite_service.domain.model.WorkEvent;
import com.omenaapp.worksite_service.domain.model.WorkEventType;
import com.omenaapp.worksite_service.domain.port.out.AssignmentRepositoryPort;
import com.omenaapp.worksite_service.domain.port.out.ProgressUpdateRepositoryPort;
import com.omenaapp.worksite_service.domain.port.out.WorkEventRepositoryPort;
import com.omenaapp.worksite_service.domain.port.out.WorksiteRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WorksiteActivityServiceTest {

    private WorksiteRepositoryPort worksites;
    private AssignmentRepositoryPort assignments;
    private WorkEventRepositoryPort events;
    private ProgressUpdateRepositoryPort progress;
    private WorksiteActivityService service;

    @BeforeEach
    void setUp() {
        worksites = mock(WorksiteRepositoryPort.class);
        assignments = mock(AssignmentRepositoryPort.class);
        events = mock(WorkEventRepositoryPort.class);
        progress = mock(ProgressUpdateRepositoryPort.class);
        service = new WorksiteActivityService(worksites, assignments, events, progress);
    }

    @Test
    void arrival_whenWorksiteMissing_throws() {
        UUID worksiteId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(worksites.existsById(worksiteId)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.arrival(worksiteId, userId));
        assertEquals("WORKSITE_NOT_FOUND", ex.getMessage());

        verify(worksites).existsById(worksiteId);
        verifyNoInteractions(assignments, events, progress);
    }

    @Test
    void arrival_whenNotAssigned_throws() {
        UUID worksiteId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(worksites.existsById(worksiteId)).thenReturn(true);
        when(assignments.existsByWorksiteIdAndUserId(worksiteId, userId)).thenReturn(false);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.arrival(worksiteId, userId));
        assertEquals("NOT_ASSIGNED", ex.getMessage());

        verify(worksites).existsById(worksiteId);
        verify(assignments).existsByWorksiteIdAndUserId(worksiteId, userId);
        verifyNoMoreInteractions(worksites, assignments);
        verifyNoInteractions(events, progress);
    }

    @Test
    void arrival_savesArrivalEvent() {
        UUID worksiteId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(worksites.existsById(worksiteId)).thenReturn(true);
        when(assignments.existsByWorksiteIdAndUserId(worksiteId, userId)).thenReturn(true);

        service.arrival(worksiteId, userId);

        ArgumentCaptor<WorkEvent> captor = ArgumentCaptor.forClass(WorkEvent.class);
        verify(events).save(captor.capture());
        WorkEvent saved = captor.getValue();

        assertNotNull(saved.id());
        assertEquals(worksiteId, saved.worksiteId());
        assertEquals(userId, saved.userId());
        assertEquals(WorkEventType.ARRIVAL, saved.type());
        assertNotNull(saved.occurredAt());

        verify(worksites).existsById(worksiteId);
        verify(assignments).existsByWorksiteIdAndUserId(worksiteId, userId);
        verifyNoMoreInteractions(worksites, assignments, events);
        verifyNoInteractions(progress);
    }

    @Test
    void departure_savesDepartureEvent() {
        UUID worksiteId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(worksites.existsById(worksiteId)).thenReturn(true);
        when(assignments.existsByWorksiteIdAndUserId(worksiteId, userId)).thenReturn(true);

        service.departure(worksiteId, userId);

        ArgumentCaptor<WorkEvent> captor = ArgumentCaptor.forClass(WorkEvent.class);
        verify(events).save(captor.capture());
        WorkEvent saved = captor.getValue();

        assertEquals(WorkEventType.DEPARTURE, saved.type());
        verifyNoInteractions(progress);
    }

    @Test
    void addProgress_emptyNote_throws() {
        UUID worksiteId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(worksites.existsById(worksiteId)).thenReturn(true);
        when(assignments.existsByWorksiteIdAndUserId(worksiteId, userId)).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.addProgress(worksiteId, userId, "   ", 10));
        assertEquals("EMPTY_NOTE", ex.getMessage());

        verifyNoInteractions(progress);
    }

    @Test
    void addProgress_invalidPercent_throws() {
        UUID worksiteId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(worksites.existsById(worksiteId)).thenReturn(true);
        when(assignments.existsByWorksiteIdAndUserId(worksiteId, userId)).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.addProgress(worksiteId, userId, "ok", 101));
        assertEquals("INVALID_PERCENT", ex.getMessage());

        verifyNoInteractions(progress);
    }

    @Test
    void addProgress_trimsNote_andSaves() {
        UUID worksiteId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(worksites.existsById(worksiteId)).thenReturn(true);
        when(assignments.existsByWorksiteIdAndUserId(worksiteId, userId)).thenReturn(true);

        service.addProgress(worksiteId, userId, "  done  ", 50);

        ArgumentCaptor<ProgressUpdate> captor = ArgumentCaptor.forClass(ProgressUpdate.class);
        verify(progress).save(captor.capture());
        ProgressUpdate saved = captor.getValue();

        assertEquals(worksiteId, saved.worksiteId());
        assertEquals(userId, saved.userId());
        assertEquals("done", saved.note());
        assertEquals(50, saved.percent());
        assertNotNull(saved.createdAt());
    }

    @Test
    void adminTimeline_mergesAndSortsDesc() {
        UUID worksiteId = UUID.randomUUID();
        UUID u1 = UUID.randomUUID();
        UUID u2 = UUID.randomUUID();

        Instant t1 = Instant.parse("2026-03-01T10:00:00Z");
        Instant t2 = Instant.parse("2026-03-01T11:00:00Z");
        Instant t3 = Instant.parse("2026-03-01T12:00:00Z");

        when(events.findByWorksiteId(worksiteId)).thenReturn(List.of(
                new WorkEvent(UUID.randomUUID(), worksiteId, u1, WorkEventType.ARRIVAL, t2),
                new WorkEvent(UUID.randomUUID(), worksiteId, u2, WorkEventType.DEPARTURE, t1)
        ));

        when(progress.findByWorksiteId(worksiteId)).thenReturn(List.of(
                new ProgressUpdate(UUID.randomUUID(), worksiteId, u1, "p", 20, t3)
        ));

        var timeline = service.adminTimeline(worksiteId);

        assertEquals(3, timeline.size());
        // sorted desc by time: t3, t2, t1
        assertEquals(t3, timeline.get(0).at());
        assertEquals("PROGRESS", timeline.get(0).kind());
        assertEquals(t2, timeline.get(1).at());
        assertEquals("ARRIVAL", timeline.get(1).kind());
        assertEquals(t1, timeline.get(2).at());
        assertEquals("DEPARTURE", timeline.get(2).kind());

        verify(events).findByWorksiteId(worksiteId);
        verify(progress).findByWorksiteId(worksiteId);
        verifyNoMoreInteractions(events, progress);
        verifyNoInteractions(worksites, assignments);
    }

    @Test
    void userTimeline_checksAccess_andUsesFilteredQueries() {
        UUID worksiteId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(worksites.existsById(worksiteId)).thenReturn(true);
        when(assignments.existsByWorksiteIdAndUserId(worksiteId, userId)).thenReturn(true);

        when(events.findByWorksiteIdAndUserId(worksiteId, userId)).thenReturn(List.of());
        when(progress.findByWorksiteIdAndUserId(worksiteId, userId)).thenReturn(List.of());

        var timeline = service.userTimeline(worksiteId, userId);
        assertNotNull(timeline);

        verify(worksites).existsById(worksiteId);
        verify(assignments).existsByWorksiteIdAndUserId(worksiteId, userId);
        verify(events).findByWorksiteIdAndUserId(worksiteId, userId);
        verify(progress).findByWorksiteIdAndUserId(worksiteId, userId);
        verifyNoMoreInteractions(worksites, assignments, events, progress);
    }
}
