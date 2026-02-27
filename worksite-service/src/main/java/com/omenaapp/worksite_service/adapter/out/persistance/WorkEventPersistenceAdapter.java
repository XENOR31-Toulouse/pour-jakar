package com.omenaapp.worksite_service.adapter.out.persistance;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.omenaapp.worksite_service.adapter.out.persistance.repos.WorkEventRepo;
import com.omenaapp.worksite_service.domain.model.WorkEvent;
import com.omenaapp.worksite_service.domain.model.WorkEventType;
import com.omenaapp.worksite_service.domain.port.out.WorkEventRepositoryPort;

@Component
public class WorkEventPersistenceAdapter implements WorkEventRepositoryPort {

    private final WorkEventRepo repo;

    public WorkEventPersistenceAdapter(WorkEventRepo repo) {
        this.repo = repo;
    }

    @Override
    public WorkEvent save(WorkEvent event) {
        var saved = repo.save(toEntity(event));
        return toDomain(saved);
    }

    @Override
    public List<WorkEvent> findByWorksiteId(UUID worksiteId) {
        return repo.findByWorksiteId(worksiteId).stream().map(WorkEventPersistenceAdapter::toDomain).toList();
    }

    @Override
    public List<WorkEvent> findByWorksiteIdAndUserId(UUID worksiteId, UUID userId) {
        return repo.findByWorksiteIdAndUserId(worksiteId, userId).stream().map(WorkEventPersistenceAdapter::toDomain).toList();
    }

    private static WorkEvent toDomain(WorkEventEntity e) {
        return new WorkEvent(e.getId(), e.getWorksiteId(), e.getUserId(), WorkEventType.valueOf(e.getType().name()), e.getOccurredAt());
    }

    private static WorkEventEntity toEntity(WorkEvent d) {
        return new WorkEventEntity(d.id(), d.worksiteId(), d.userId(), com.omenaapp.worksite_service.adapter.out.persistance.WorkEventType.valueOf(d.type().name()), d.occurredAt());
    }
}
