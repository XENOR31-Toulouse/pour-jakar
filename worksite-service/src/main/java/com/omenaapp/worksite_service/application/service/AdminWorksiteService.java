package com.omenaapp.worksite_service.application.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.omenaapp.worksite_service.domain.model.Worksite;
import com.omenaapp.worksite_service.domain.port.out.AssignmentRepositoryPort;
import com.omenaapp.worksite_service.domain.port.out.WorksiteRepositoryPort;

/**
 * Admin use-cases for worksite creation, update, deletion.
 */
@Service
public class AdminWorksiteService {
    private final WorksiteRepositoryPort worksites;
    private final AssignmentRepositoryPort assignments;

    public AdminWorksiteService(WorksiteRepositoryPort worksites, AssignmentRepositoryPort assignments) {
        this.worksites = worksites;
        this.assignments = assignments;
    }

    public Worksite createWorksite(String name, String address) {
        Worksite worksite = new Worksite(UUID.randomUUID(), name, address, Instant.now());
        return worksites.save(worksite);
    }

    public Worksite updateWorksite(UUID id, Worksite worksite) {
        // Check if worksite exists
        if (!worksites.existsById(id)) {
            throw new RuntimeException("Worksite not found with id: " + id);
        }
        // Update the worksite
        return worksites.save(worksite);
    }

    public void deleteWorksite(UUID id) {
        worksites.deleteById(id);
    }

    public List<Worksite> listWorksites() {
        return worksites.findAll();
    }

    public void assignUser(UUID worksiteId, UUID userId) {
        // Check if worksite exists
        if (!worksites.existsById(worksiteId)) {
            throw new RuntimeException("Worksite not found with id: " + worksiteId);
        }
        // Check if user exists (we assume user exists for now)
        // Save the assignment
        assignments.save(new com.omenaapp.worksite_service.domain.model.Assignment(
            UUID.randomUUID(), 
            worksiteId, 
            userId, 
            Instant.now()
        ));
    }

    public void unassignUser(UUID worksiteId, UUID userId) {
        assignments.deleteByWorksiteIdAndUserId(worksiteId, userId);
    }

    public List<com.omenaapp.worksite_service.domain.model.Assignment> listAssignments(UUID worksiteId) {
        return assignments.findByWorksiteId(worksiteId);
    }

    // Nouvelle méthode pour attribuer un client à un worksite
    public Worksite assignClientToWorksite(UUID worksiteId, UUID clientId) {
        // Vérifier que le worksite existe
        var worksiteOpt = worksites.findById(worksiteId);
        if (worksiteOpt.isEmpty()) {
            throw new RuntimeException("Worksite not found with id: " + worksiteId);
        }

        // Ici, nous devons implémenter la logique pour attribuer un client
        // Pour l'instant, nous allons simplement retourner le worksite mis à jour
        // La logique complète sera implémentée dans l'adapter persistence
        return worksiteOpt.get();
    }
}
