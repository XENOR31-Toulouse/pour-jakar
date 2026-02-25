package com.omenaapp.worksite_service.adapter.in.web;

import java.time.Instant;
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

import com.omenaapp.worksite_service.adapter.out.persistance.AssignmentEntity;
import com.omenaapp.worksite_service.adapter.out.persistance.WorksiteEntity;
import com.omenaapp.worksite_service.domain.AssignmentRepo;
import com.omenaapp.worksite_service.domain.WorksiteRepo;



@RestController
@RequestMapping("/admin/worksites")
public class AdminWorksiteController {

  private final WorksiteRepo worksites;
  private final AssignmentRepo assignments;

  public AdminWorksiteController(WorksiteRepo worksites, AssignmentRepo assignments) {
    this.worksites = worksites;
    this.assignments = assignments;
  }

  public record WorksiteDto(UUID id, String name, String address) {}
  public record CreateReq(String name, String address) {}
  public record CreateRes(UUID id) {}
  public record AssignmentDto(UUID userId, Instant assignedAt) {}

  @GetMapping
  public List<WorksiteDto> list() {
    return worksites.findAll().stream()
      .map(w -> new WorksiteDto(w.getId(), w.getName(), w.getAddress()))
      .toList();
  }

  @PostMapping
  public ResponseEntity<CreateRes> create(@RequestBody CreateReq req) {
    if (req.name() == null || req.name().trim().length() < 2) return ResponseEntity.badRequest().build();
    UUID id = UUID.randomUUID();
    worksites.save(new WorksiteEntity(id, req.name().trim(), req.address(), Instant.now()));
    return ResponseEntity.ok(new CreateRes(id));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(@PathVariable UUID id) {
    if (worksites.findById(id).isEmpty()) return ResponseEntity.notFound().build();
    worksites.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{worksiteId}/assign/{userId}")
  public ResponseEntity<?> assign(@PathVariable UUID worksiteId, @PathVariable UUID userId) {
    if (worksites.findById(worksiteId).isEmpty()) return ResponseEntity.notFound().build();
    try {
      assignments.save(new AssignmentEntity(UUID.randomUUID(), worksiteId, userId, Instant.now()));
    } catch (Exception ignored) {}
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{worksiteId}/assign/{userId}")
  public ResponseEntity<?> unassign(@PathVariable UUID worksiteId, @PathVariable UUID userId) {
    assignments.deleteByWorksiteIdAndUserId(worksiteId, userId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{worksiteId}/assignments")
  public List<AssignmentDto> listAssignments(@PathVariable UUID worksiteId) {
    return assignments.findByWorksiteId(worksiteId).stream()
      .map(a -> new AssignmentDto(a.getUserId(), a.getAssignedAt()))
      .toList();
  }
}