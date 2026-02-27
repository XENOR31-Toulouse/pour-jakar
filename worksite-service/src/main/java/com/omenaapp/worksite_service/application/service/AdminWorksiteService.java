package com.omenaapp.worksite_service.application.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.omenaapp.worksite_service.domain.model.Assignment;
import com.omenaapp.worksite_service.domain.model.Worksite;
import com.omenaapp.worksite_service.domain.port.out.AssignmentRepositoryPort;
import com.omenaapp.worksite_service.domain.port.out.WorksiteRepositoryPort;

/**
 * Application service (use-cases) for admin operations.
 * No Spring annotations here: wired in infrastructure config.
 */
public class AdminWorksiteService {

    private final WorksiteRepositoryPort worksites;
    private final AssignmentRepositoryPort assignments;

    public AdminWorksiteService(WorksiteRepositoryPort worksites, AssignmentRepositoryPort assignments) {
        this.worksites = worksites;
        this.assignments = assignments;
    }

    public List<Worksite> listWorksites() {
        return worksites.findAll();
    }

    public UUID createWorksite(String name, String address) {
        if (name == null || name.trim().length() < 2) {
            throw new IllegalArgumentException("INVALID_NAME");
        }
        UUID id = UUID.randomUUID();
        worksites.save(new Worksite(id, name.trim(), address, Instant.now()));
        return id;
    }

    public void deleteWorksite(UUID id) {
        if (!worksites.existsById(id)) {
            throw new IllegalArgumentException("WORKSITE_NOT_FOUND");
        }
        worksites.deleteById(id);
    }

    public void assignUser(UUID worksiteId, UUID userId) {
        if (!worksites.existsById(worksiteId)) {
            throw new IllegalArgumentException("WORKSITE_NOT_FOUND");
        }
        // Best-effort: ignore duplicates.
        try {
            assignments.save(new Assignment(UUID.randomUUID(), worksiteId, userId, Instant.now()));
        } catch (Exception ignored) {
        }
    }

    public void unassignUser(UUID worksiteId, UUID userId) {
        assignments.deleteByWorksiteIdAndUserId(worksiteId, userId);
    }

    public List<Assignment> listAssignments(UUID worksiteId) {
        return assignments.findByWorksiteId(worksiteId);
    }
}
