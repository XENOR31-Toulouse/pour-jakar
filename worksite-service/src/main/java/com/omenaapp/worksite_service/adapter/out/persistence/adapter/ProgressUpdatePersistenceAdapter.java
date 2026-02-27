package com.omenaapp.worksite_service.adapter.out.persistence.adapter;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.omenaapp.worksite_service.adapter.out.persistence.repository.ProgressUpdateRepo;
import com.omenaapp.worksite_service.domain.model.ProgressUpdate;
import com.omenaapp.worksite_service.domain.port.out.ProgressUpdateRepositoryPort;
import com.omenaapp.worksite_service.adapter.out.persistence.entity.ProgressUpdateEntity;

@Component
public class ProgressUpdatePersistenceAdapter implements ProgressUpdateRepositoryPort {

    private final ProgressUpdateRepo repo;

    public ProgressUpdatePersistenceAdapter(ProgressUpdateRepo repo) {
        this.repo = repo;
    }

    @Override
    public ProgressUpdate save(ProgressUpdate update) {
        var saved = repo.save(toEntity(update));
        return toDomain(saved);
    }

    @Override
    public List<ProgressUpdate> findByWorksiteId(UUID worksiteId) {
        return repo.findByWorksiteId(worksiteId).stream().map(ProgressUpdatePersistenceAdapter::toDomain).toList();
    }

    @Override
    public List<ProgressUpdate> findByWorksiteIdAndUserId(UUID worksiteId, UUID userId) {
        return repo.findByWorksiteIdAndUserId(worksiteId, userId).stream().map(ProgressUpdatePersistenceAdapter::toDomain).toList();
    }

    private static ProgressUpdate toDomain(ProgressUpdateEntity e) {
        return new ProgressUpdate(e.getId(), e.getWorksiteId(), e.getUserId(), e.getNote(), e.getPercent(), e.getCreatedAt());
    }

    private static ProgressUpdateEntity toEntity(ProgressUpdate d) {
        return new ProgressUpdateEntity(d.id(), d.worksiteId(), d.userId(), d.note(), d.percent(), d.createdAt());
    }
}
