package com.omenaapp.worksite_service.adapter.out.persistence.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.omenaapp.worksite_service.adapter.out.persistence.entity.ClientEntity;
import com.omenaapp.worksite_service.adapter.out.persistence.entity.WorksiteEntity;
import com.omenaapp.worksite_service.adapter.out.persistence.repository.ClientRepo;
import com.omenaapp.worksite_service.adapter.out.persistence.repository.WorksiteRepo;
import com.omenaapp.worksite_service.domain.model.Worksite;
import com.omenaapp.worksite_service.domain.port.out.WorksiteRepositoryPort;

@Component
public class WorksitePersistenceAdapter implements WorksiteRepositoryPort {
    private final WorksiteRepo repo;
    private final ClientRepo clientRepo;
    
    public WorksitePersistenceAdapter(WorksiteRepo repo, ClientRepo clientRepo) {
        this.repo = repo;
        this.clientRepo = clientRepo;
    }

    @Override
    public List<Worksite> findAll() {
        return repo.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Worksite> findById(UUID id) {
        return repo.findById(id).map(this::toDomain);
    }

    @Override
    public boolean existsById(UUID id) {
        return repo.existsById(id);
    }

    @Override
    public Worksite save(Worksite worksite) {
        WorksiteEntity entity = toEntity(worksite);
        WorksiteEntity savedEntity = repo.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public void deleteById(UUID id) {
        repo.deleteById(id);
    }
    
    // Méthode pour attribuer un client à un worksite
    public Worksite assignClientToWorksite(UUID worksiteId, UUID clientId) {
        WorksiteEntity worksiteEntity = repo.findById(worksiteId)
                .orElseThrow(() -> new RuntimeException("Worksite not found with id: " + worksiteId));
        
        ClientEntity clientEntity = clientRepo.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + clientId));
        
        worksiteEntity.setClient(clientEntity);
        WorksiteEntity savedEntity = repo.save(worksiteEntity);
        return toDomain(savedEntity);
    }
    
    private Worksite toDomain(WorksiteEntity entity) {
        return new Worksite(
                entity.getId(),
                entity.getName(),
                entity.getAddress(),
                entity.getCreatedAt()
        );
    }
    
    private WorksiteEntity toEntity(Worksite worksite) {
        WorksiteEntity entity = new WorksiteEntity(
                worksite.id(),
                worksite.name(),
                worksite.address(),
                worksite.createdAt()
        );
        return entity;
    }
}
