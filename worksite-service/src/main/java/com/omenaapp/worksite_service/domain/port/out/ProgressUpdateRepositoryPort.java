package com.omenaapp.worksite_service.domain.port.out;

import java.util.List;
import java.util.UUID;

import com.omenaapp.worksite_service.domain.model.ProgressUpdate;

public interface ProgressUpdateRepositoryPort {
    ProgressUpdate save(ProgressUpdate update);
    List<ProgressUpdate> findByWorksiteId(UUID worksiteId);
    List<ProgressUpdate> findByWorksiteIdAndUserId(UUID worksiteId, UUID userId);
}
