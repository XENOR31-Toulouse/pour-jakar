package com.omenaapp.worksite_service.adapter.in.web;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.omenaapp.worksite_service.domain.ProgressUpdateRepo;
import com.omenaapp.worksite_service.domain.WorkEventRepo;

@RestController
@RequestMapping("/admin/worksites")
public class AdminTimelineController {

  private final WorkEventRepo events;
  private final ProgressUpdateRepo progress;

  public AdminTimelineController(WorkEventRepo events, ProgressUpdateRepo progress) {
    this.events = events;
    this.progress = progress;
  }

  public record TimelineItem(
      String kind,
      UUID userId,
      Instant at,
      String note,
      Integer percent
  ) {}

  @GetMapping("/{worksiteId}/timeline")
  public List<TimelineItem> timeline(@PathVariable UUID worksiteId) {

    var e = events.findByWorksiteId(worksiteId).stream()
        .map(x -> new TimelineItem(x.getType().name(), x.getUserId(), x.getOccurredAt(), null, null))
        .toList();

    var p = progress.findByWorksiteId(worksiteId).stream()
        .map(x -> new TimelineItem("PROGRESS", x.getUserId(), x.getCreatedAt(), x.getNote(), x.getPercent()))
        .toList();

    return java.util.stream.Stream.concat(e.stream(), p.stream())
        .sorted(Comparator.comparing(TimelineItem::at).reversed())
        .toList();
  }
}