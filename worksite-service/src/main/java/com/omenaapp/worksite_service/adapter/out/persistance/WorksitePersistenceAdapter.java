package com.omenaapp.worksite_service.adapter.out.persistance;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.omenaapp.worksite_service.adapter.out.persistance.repos.WorksiteRepo;
import com.omenaapp.worksite_service.domain.model.Worksite;
import com.omenaapp.worksite_service.domain.port.out.WorksiteRepositoryPort;

@Component
public class WorksitePersistenceAdapter implements WorksiteRepositoryPort {

    private final WorksiteRepo repo;

    public WorksitePersistenceAdapter(WorksiteRepo repo) {
        this.repo = repo;
    }

    @Override
    public List<Worksite> findAll() {
        return repo.findAll().stream().map(WorksitePersistenceAdapter::toDomain).toList();
    }

    @Override
    public Optional<Worksite> findById(UUID id) {
        return repo.findById(id).map(WorksitePersistenceAdapter::toDomain);
    }

    @Override
    public boolean existsById(UUID id) {
        return repo.existsById(id);
    }

    @Override
    public Worksite save(Worksite worksite) {
        var saved = repo.save(toEntity(worksite));
        return toDomain(saved);
    }

    @Override
    public void deleteById(UUID id) {
        repo.deleteById(id);
    }

    private static Worksite toDomain(WorksiteEntity e) {
        return new Worksite(e.getId(), e.getName(), e.getAddress(), e.getCreatedAt());
    }

    private static WorksiteEntity toEntity(Worksite d) {
        return new WorksiteEntity(d.id(), d.name(), d.address(), d.createdAt());
    }
}
