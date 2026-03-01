package com.omenaapp.worksite_service.adapter.in.web;



import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.omenaapp.worksite_service.application.service.WorksiteActivityService;


@RestController
@RequestMapping("/api/worksites")
public class UserWorksiteActionsController {

  private final WorksiteActivityService service;

  public UserWorksiteActionsController(
      WorksiteActivityService service
  ) {
    this.service = service;
  }

  public record ProgressReq(String note, Integer percent) {}

  // timeline items (union)
  public record TimelineItem(
      String kind,            // "ARRIVAL" | "DEPARTURE" | "PROGRESS"
      UUID userId,
      java.time.Instant at,
      String note,
      Integer percent
  ) {}

  @PostMapping("/{worksiteId}/arrival")
  public ResponseEntity<?> arrival(@PathVariable UUID worksiteId) {
    UUID userId = currentUserId();
    service.arrival(worksiteId, userId);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/{worksiteId}/departure")
  public ResponseEntity<?> departure(@PathVariable UUID worksiteId) {
    UUID userId = currentUserId();
    service.departure(worksiteId, userId);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/{worksiteId}/progress")
  public ResponseEntity<?> addProgress(@PathVariable UUID worksiteId, @RequestBody ProgressReq req) {
    UUID userId = currentUserId();
    try {
      service.addProgress(worksiteId, userId, req.note(), req.percent());
      return ResponseEntity.ok().build();
    } catch (IllegalArgumentException ex) {
      return ResponseEntity.badRequest().build();
    }
  }

  // USER timeline = seulement ses events à lui
  @GetMapping("/{worksiteId}/timeline")
  public List<TimelineItem> myTimeline(@PathVariable UUID worksiteId) {
    UUID userId = currentUserId();
    return service.userTimeline(worksiteId, userId).stream()
        .map(x -> new TimelineItem(x.kind(), x.userId(), x.at(), x.note(), x.percent()))
        .sorted(Comparator.comparing(TimelineItem::at).reversed())
        .toList();
  }

  private UUID currentUserId() {
    String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return UUID.fromString(principal);
  }

}
