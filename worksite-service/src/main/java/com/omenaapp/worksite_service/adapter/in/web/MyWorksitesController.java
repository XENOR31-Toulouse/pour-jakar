package com.omenaapp.worksite_service.adapter.in.web;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.omenaapp.worksite_service.domain.AssignmentRepo;
import com.omenaapp.worksite_service.domain.WorksiteRepo;



@RestController
@RequestMapping("/api")
public class MyWorksitesController {

  private final AssignmentRepo assignments;
  private final WorksiteRepo worksites;

  public MyWorksitesController(AssignmentRepo assignments, WorksiteRepo worksites) {
    this.assignments = assignments;
    this.worksites = worksites;
  }

  public record WorksiteDto(UUID id, String name, String address) {}

  @GetMapping("/my-worksites")
  public List<WorksiteDto> myWorksites() {
    String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    UUID userId = UUID.fromString(principal);

    return assignments.findByUserId(userId).stream()
      .map(a -> worksites.findById(a.getWorksiteId()).orElse(null))
      .filter(ws -> ws != null)
      .map(ws -> new WorksiteDto(ws.getId(), ws.getName(), ws.getAddress()))
      .toList();
  }
}