package com.omenaapp.worksite_service.domain.port.out;

import java.util.List;
import java.util.UUID;

import com.omenaapp.worksite_service.domain.model.Assignment;

public interface AssignmentRepositoryPort {
    Assignment save(Assignment assignment);
    List<Assignment> findByUserId(UUID userId);
    List<Assignment> findByWorksiteId(UUID worksiteId);
    boolean existsByWorksiteIdAndUserId(UUID worksiteId, UUID userId);
    void deleteByWorksiteIdAndUserId(UUID worksiteId, UUID userId);
}
