package com.omenaapp.worksite_service.adapter.in.web;



import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.omenaapp.worksite_service.domain.AssignmentRepo;
import com.omenaapp.worksite_service.domain.WorksiteRepo;
import com.omenaapp.worksite_service.domain.WorkEventRepo;
import com.omenaapp.worksite_service.domain.ProgressUpdateRepo;
import com.omenaapp.worksite_service.adapter.out.persistance.WorkEventEntity;
import com.omenaapp.worksite_service.adapter.out.persistance.ProgressUpdateEntity;
import com.omenaapp.worksite_service.adapter.out.persistance.WorkEventType;


@RestController
@RequestMapping("/api/worksites")
public class UserWorksiteActionsController {

  private final WorksiteRepo worksites;
  private final AssignmentRepo assignments;
  private final WorkEventRepo events;
  private final ProgressUpdateRepo progress;

  public UserWorksiteActionsController(
      WorksiteRepo worksites,
      AssignmentRepo assignments,
      WorkEventRepo events,
      ProgressUpdateRepo progress
  ) {
    this.worksites = worksites;
    this.assignments = assignments;
    this.events = events;
    this.progress = progress;
  }

  public record ProgressReq(String note, Integer percent) {}

  // timeline items (union)
  public record TimelineItem(
      String kind,            // "ARRIVAL" | "DEPARTURE" | "PROGRESS"
      UUID userId,
      Instant at,
      String note,
      Integer percent
  ) {}

  @PostMapping("/{worksiteId}/arrival")
  public ResponseEntity<?> arrival(@PathVariable UUID worksiteId) {
    UUID userId = currentUserId();
    assertCanAccess(worksiteId, userId);

    events.save(new WorkEventEntity(UUID.randomUUID(), worksiteId, userId, WorkEventType.ARRIVAL, Instant.now()));
    return ResponseEntity.ok().build();
  }

  @PostMapping("/{worksiteId}/departure")
  public ResponseEntity<?> departure(@PathVariable UUID worksiteId) {
    UUID userId = currentUserId();
    assertCanAccess(worksiteId, userId);

    events.save(new WorkEventEntity(UUID.randomUUID(), worksiteId, userId, WorkEventType.DEPARTURE, Instant.now()));
    return ResponseEntity.ok().build();
  }

  @PostMapping("/{worksiteId}/progress")
  public ResponseEntity<?> addProgress(@PathVariable UUID worksiteId, @RequestBody ProgressReq req) {
    UUID userId = currentUserId();
    assertCanAccess(worksiteId, userId);

    String note = req.note() == null ? "" : req.note().trim();
    if (note.isEmpty()) return ResponseEntity.badRequest().build();

    Integer percent = req.percent();
    if (percent != null && (percent < 0 || percent > 100)) return ResponseEntity.badRequest().build();

    progress.save(new ProgressUpdateEntity(UUID.randomUUID(), worksiteId, userId, note, percent, Instant.now()));
    return ResponseEntity.ok().build();
  }

  // USER timeline = seulement ses events à lui
  @GetMapping("/{worksiteId}/timeline")
  public List<TimelineItem> myTimeline(@PathVariable UUID worksiteId) {
    UUID userId = currentUserId();
    assertCanAccess(worksiteId, userId);

    var e = events.findByWorksiteIdAndUserId(worksiteId, userId).stream()
        .map(x -> new TimelineItem(x.getType().name(), x.getUserId(), x.getOccurredAt(), null, null))
        .toList();

    var p = progress.findByWorksiteIdAndUserId(worksiteId, userId).stream()
        .map(x -> new TimelineItem("PROGRESS", x.getUserId(), x.getCreatedAt(), x.getNote(), x.getPercent()))
        .toList();

    return concatAndSort(e, p);
  }

  private static List<TimelineItem> concatAndSort(List<TimelineItem> a, List<TimelineItem> b) {
    return java.util.stream.Stream.concat(a.stream(), b.stream())
        .sorted(Comparator.comparing(TimelineItem::at).reversed())
        .toList();
  }

  private UUID currentUserId() {
    String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return UUID.fromString(principal);
  }

  private void assertCanAccess(UUID worksiteId, UUID userId) {
    if (worksites.findById(worksiteId).isEmpty()) {
      throw new IllegalArgumentException("WORKSITE_NOT_FOUND");
    }
    boolean assigned = assignments.existsByWorksiteIdAndUserId(worksiteId, userId);
    if (!assigned) {
      // 403 serait idéal → pour MVP, on throw et on mappe en 403 via handler si tu veux
      throw new IllegalStateException("NOT_ASSIGNED");
    }
  }
}
