package com.omenaapp.worksite_service.adapter.in.web;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.omenaapp.worksite_service.application.service.MyWorksitesService;



@RestController
@RequestMapping("/api")
public class MyWorksitesController {

  private final MyWorksitesService service;

  public MyWorksitesController(MyWorksitesService service) {
    this.service = service;
  }

  public record WorksiteDto(UUID id, String name, String address) {}

  @GetMapping("/my-worksites")
  public List<WorksiteDto> myWorksites() {
    String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    UUID userId = UUID.fromString(principal);

    return service.listMyWorksites(userId).stream()
      .map(ws -> new WorksiteDto(ws.id(), ws.name(), ws.address()))
      .toList();
  }
}
