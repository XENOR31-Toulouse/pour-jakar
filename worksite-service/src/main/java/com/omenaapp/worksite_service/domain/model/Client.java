package com.omenaapp.worksite_service.domain.model;

import java.time.Instant;
import java.util.UUID;

/** Domain model (no Spring/JPA annotations). */
public record Client(
    UUID id,
    String name,
    String email,
    String phoneNumber,
    String address,
    Instant createdAt
) {}