package com.omenaapp.worksite_service.adapter.out.persistance;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.omenaapp.worksite_service.adapter.out.persistance.repos.AssignmentRepo;
import com.omenaapp.worksite_service.domain.model.Assignment;
import com.omenaapp.worksite_service.domain.port.out.AssignmentRepositoryPort;

@Component
public class AssignmentPersistenceAdapter implements AssignmentRepositoryPort {

    private final AssignmentRepo repo;

    public AssignmentPersistenceAdapter(AssignmentRepo repo) {
        this.repo = repo;
    }

    @Override
    public Assignment save(Assignment assignment) {
        var saved = repo.save(toEntity(assignment));
        return toDomain(saved);
    }

    @Override
    public List<Assignment> findByUserId(UUID userId) {
        return repo.findByUserId(userId).stream().map(AssignmentPersistenceAdapter::toDomain).toList();
    }

    @Override
    public List<Assignment> findByWorksiteId(UUID worksiteId) {
        return repo.findByWorksiteId(worksiteId).stream().map(AssignmentPersistenceAdapter::toDomain).toList();
    }

    @Override
    public boolean existsByWorksiteIdAndUserId(UUID worksiteId, UUID userId) {
        return repo.existsByWorksiteIdAndUserId(worksiteId, userId);
    }

    @Override
    public void deleteByWorksiteIdAndUserId(UUID worksiteId, UUID userId) {
        repo.deleteByWorksiteIdAndUserId(worksiteId, userId);
    }

    private static Assignment toDomain(AssignmentEntity e) {
        return new Assignment(e.getId(), e.getWorksiteId(), e.getUserId(), e.getAssignedAt());
    }

    private static AssignmentEntity toEntity(Assignment d) {
        return new AssignmentEntity(d.id(), d.worksiteId(), d.userId(), d.assignedAt());
    }
}
