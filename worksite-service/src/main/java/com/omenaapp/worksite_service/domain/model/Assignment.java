package com.omenaapp.worksite_service.domain.model;

import java.time.Instant;
import java.util.UUID;

public record Assignment(
    UUID id,
    UUID worksiteId,
    UUID userId,
    Instant assignedAt
) {}
