package com.omenaapp.worksite_service.domain.model;

import java.time.Instant;
import java.util.UUID;

/** Domain model (no Spring/JPA annotations). */
public record Worksite(
    UUID id,
    String name,
    String address,
    Instant createdAt
) {}
