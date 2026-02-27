package com.omenaapp.worksite_service.application.service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import com.omenaapp.worksite_service.domain.model.ProgressUpdate;
import com.omenaapp.worksite_service.domain.model.WorkEvent;
import com.omenaapp.worksite_service.domain.model.WorkEventType;
import com.omenaapp.worksite_service.domain.port.out.AssignmentRepositoryPort;
import com.omenaapp.worksite_service.domain.port.out.ProgressUpdateRepositoryPort;
import com.omenaapp.worksite_service.domain.port.out.WorkEventRepositoryPort;
import com.omenaapp.worksite_service.domain.port.out.WorksiteRepositoryPort;

/** Use-cases for arrival/departure/progress + timeline queries. */
public class WorksiteActivityService {

    public record TimelineItem(
        String kind,
        UUID userId,
        Instant at,
        String note,
        Integer percent
    ) {}

    private final WorksiteRepositoryPort worksites;
    private final AssignmentRepositoryPort assignments;
    private final WorkEventRepositoryPort events;
    private final ProgressUpdateRepositoryPort progress;

    public WorksiteActivityService(
        WorksiteRepositoryPort worksites,
        AssignmentRepositoryPort assignments,
        WorkEventRepositoryPort events,
        ProgressUpdateRepositoryPort progress
    ) {
        this.worksites = worksites;
        this.assignments = assignments;
        this.events = events;
        this.progress = progress;
    }

    public void arrival(UUID worksiteId, UUID userId) {
        assertCanAccess(worksiteId, userId);
        events.save(new WorkEvent(UUID.randomUUID(), worksiteId, userId, WorkEventType.ARRIVAL, Instant.now()));
    }

    public void departure(UUID worksiteId, UUID userId) {
        assertCanAccess(worksiteId, userId);
        events.save(new WorkEvent(UUID.randomUUID(), worksiteId, userId, WorkEventType.DEPARTURE, Instant.now()));
    }

    public void addProgress(UUID worksiteId, UUID userId, String note, Integer percent) {
        assertCanAccess(worksiteId, userId);
        String n = note == null ? "" : note.trim();
        if (n.isEmpty()) {
            throw new IllegalArgumentException("EMPTY_NOTE");
        }
        if (percent != null && (percent < 0 || percent > 100)) {
            throw new IllegalArgumentException("INVALID_PERCENT");
        }
        progress.save(new ProgressUpdate(UUID.randomUUID(), worksiteId, userId, n, percent, Instant.now()));
    }

    public List<TimelineItem> adminTimeline(UUID worksiteId) {
        var e = events.findByWorksiteId(worksiteId).stream()
            .map(x -> new TimelineItem(x.type().name(), x.userId(), x.occurredAt(), null, null))
            .toList();
        var p = progress.findByWorksiteId(worksiteId).stream()
            .map(x -> new TimelineItem("PROGRESS", x.userId(), x.createdAt(), x.note(), x.percent()))
            .toList();
        return concatAndSort(e, p);
    }

    public List<TimelineItem> userTimeline(UUID worksiteId, UUID userId) {
        assertCanAccess(worksiteId, userId);
        var e = events.findByWorksiteIdAndUserId(worksiteId, userId).stream()
            .map(x -> new TimelineItem(x.type().name(), x.userId(), x.occurredAt(), null, null))
            .toList();
        var p = progress.findByWorksiteIdAndUserId(worksiteId, userId).stream()
            .map(x -> new TimelineItem("PROGRESS", x.userId(), x.createdAt(), x.note(), x.percent()))
            .toList();
        return concatAndSort(e, p);
    }

    private static List<TimelineItem> concatAndSort(List<TimelineItem> a, List<TimelineItem> b) {
        return java.util.stream.Stream.concat(a.stream(), b.stream())
            .sorted(Comparator.comparing(TimelineItem::at).reversed())
            .toList();
    }

    private void assertCanAccess(UUID worksiteId, UUID userId) {
        if (!worksites.existsById(worksiteId)) {
            throw new IllegalArgumentException("WORKSITE_NOT_FOUND");
        }
        boolean assigned = assignments.existsByWorksiteIdAndUserId(worksiteId, userId);
        if (!assigned) {
            throw new IllegalStateException("NOT_ASSIGNED");
        }
    }
}
