package com.omenaapp.worksite_service.domain.port.out;

import java.util.List;
import java.util.UUID;

import com.omenaapp.worksite_service.domain.model.WorkEvent;

public interface WorkEventRepositoryPort {
    WorkEvent save(WorkEvent event);
    List<WorkEvent> findByWorksiteId(UUID worksiteId);
    List<WorkEvent> findByWorksiteIdAndUserId(UUID worksiteId, UUID userId);
}
