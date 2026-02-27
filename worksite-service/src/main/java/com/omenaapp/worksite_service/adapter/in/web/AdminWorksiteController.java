package com.omenaapp.worksite_service.adapter.in.web;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.omenaapp.worksite_service.application.service.AdminWorksiteService;



@RestController
@RequestMapping("/admin/worksites")
public class AdminWorksiteController {

  private final AdminWorksiteService service;

  public AdminWorksiteController(AdminWorksiteService service) {
    this.service = service;
  }

  public record WorksiteDto(UUID id, String name, String address) {}
  public record CreateReq(String name, String address) {}
  public record CreateRes(UUID id) {}
  public record AssignmentDto(UUID userId, java.time.Instant assignedAt) {}

  @GetMapping
  public List<WorksiteDto> list() {
    return service.listWorksites().stream()
      .map(w -> new WorksiteDto(w.id(), w.name(), w.address()))
      .toList();
  }

  @PostMapping
  public ResponseEntity<CreateRes> create(@RequestBody CreateReq req) {
    try {
      UUID id = service.createWorksite(req.name(), req.address());
      return ResponseEntity.ok(new CreateRes(id));
    } catch (IllegalArgumentException ex) {
      return ResponseEntity.badRequest().build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(@PathVariable UUID id) {
    try {
      service.deleteWorksite(id);
      return ResponseEntity.noContent().build();
    } catch (IllegalArgumentException ex) {
      return ResponseEntity.notFound().build();
    }
  }

  @PostMapping("/{worksiteId}/assign/{userId}")
  public ResponseEntity<?> assign(@PathVariable UUID worksiteId, @PathVariable UUID userId) {
    try {
      service.assignUser(worksiteId, userId);
      return ResponseEntity.ok().build();
    } catch (IllegalArgumentException ex) {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{worksiteId}/assign/{userId}")
  public ResponseEntity<?> unassign(@PathVariable UUID worksiteId, @PathVariable UUID userId) {
    service.unassignUser(worksiteId, userId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{worksiteId}/assignments")
  public List<AssignmentDto> listAssignments(@PathVariable UUID worksiteId) {
    return service.listAssignments(worksiteId).stream()
      .map(a -> new AssignmentDto(a.userId(), a.assignedAt()))
      .toList();
  }
}
