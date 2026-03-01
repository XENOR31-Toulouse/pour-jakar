package com.omenaapp.worksite_service.application.service;

import java.util.List;
import java.util.UUID;

import com.omenaapp.worksite_service.domain.model.Worksite;
import com.omenaapp.worksite_service.domain.port.out.AssignmentRepositoryPort;
import com.omenaapp.worksite_service.domain.port.out.WorksiteRepositoryPort;

public class MyWorksitesService {

    private final AssignmentRepositoryPort assignments;
    private final WorksiteRepositoryPort worksites;

    public MyWorksitesService(AssignmentRepositoryPort assignments, WorksiteRepositoryPort worksites) {
        this.assignments = assignments;
        this.worksites = worksites;
    }

    public List<Worksite> listMyWorksites(UUID userId) {
        return assignments.findByUserId(userId).stream()
            .map(a -> worksites.findById(a.worksiteId()).orElse(null))
            .filter(ws -> ws != null)
            .toList();
    }
}
