package com.omenaapp.worksite_service.application.service;

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

    public Worksite createWorksite(Worksite worksite) {
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

