package com.omenaapp.worksite_service.adapter.in.web;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.omenaapp.worksite_service.application.service.AdminWorksiteService;

@RestController
@RequestMapping("/api/worksites")
public class WorksiteClientController {
    
    private final AdminWorksiteService adminWorksiteService;
    
    public WorksiteClientController(AdminWorksiteService adminWorksiteService) {
        this.adminWorksiteService = adminWorksiteService;
    }
    
    @PostMapping("/{worksiteId}/assign-client/{clientId}")
    public ResponseEntity<Void> assignClientToWorksite(@PathVariable UUID worksiteId, @PathVariable UUID clientId) {
        adminWorksiteService.assignClientToWorksite(worksiteId, clientId);
        return ResponseEntity.ok().build();
    }
}