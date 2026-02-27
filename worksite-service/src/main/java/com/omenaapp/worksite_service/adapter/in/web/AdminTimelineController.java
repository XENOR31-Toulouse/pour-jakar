package com.omenaapp.worksite_service.adapter.in.web;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.omenaapp.worksite_service.application.service.WorksiteActivityService;

@RestController
@RequestMapping("/admin/worksites")
public class AdminTimelineController {

  private final WorksiteActivityService service;

  public AdminTimelineController(WorksiteActivityService service) {
    this.service = service;
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
    return service.adminTimeline(worksiteId).stream()
        .map(x -> new TimelineItem(x.kind(), x.userId(), x.at(), x.note(), x.percent()))
        .sorted(Comparator.comparing(TimelineItem::at).reversed())
        .toList();
  }
}
