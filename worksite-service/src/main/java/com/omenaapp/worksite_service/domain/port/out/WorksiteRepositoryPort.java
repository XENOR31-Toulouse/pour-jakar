package com.omenaapp.worksite_service.domain.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.omenaapp.worksite_service.domain.model.Worksite;

public interface WorksiteRepositoryPort {
    List<Worksite> findAll();
    Optional<Worksite> findById(UUID id);
    boolean existsById(UUID id);
    Worksite save(Worksite worksite);
    void deleteById(UUID id);
}
