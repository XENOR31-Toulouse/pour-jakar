package com.omenaapp.worksite_service.domain.model;

import java.time.Instant;
import java.util.UUID;

public record WorkEvent(
    UUID id,
    UUID worksiteId,
    UUID userId,
    WorkEventType type,
    Instant occurredAt
) {}
