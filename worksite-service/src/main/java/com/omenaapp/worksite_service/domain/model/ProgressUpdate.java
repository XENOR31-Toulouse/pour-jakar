package com.omenaapp.worksite_service.domain.model;

import java.time.Instant;
import java.util.UUID;

public record ProgressUpdate(
    UUID id,
    UUID worksiteId,
    UUID userId,
    String note,
    Integer percent,
    Instant createdAt
) {}
