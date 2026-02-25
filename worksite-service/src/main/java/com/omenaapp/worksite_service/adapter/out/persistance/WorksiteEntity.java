package com.omenaapp.worksite_service.adapter.out.persistance;



import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="worksites")
public class WorksiteEntity {
  @Id
  private UUID id;

  @Column(nullable=false)
  private String name;

  private String address;

  @Column(nullable=false)
  private Instant createdAt;

  protected WorksiteEntity() {}

  public WorksiteEntity(UUID id, String name, String address, Instant createdAt) {
    this.id = id;
    this.name = name;
    this.address = address;
    this.createdAt = createdAt;
  }

  public UUID getId() { return id; }
  public String getName() { return name; }
  public String getAddress() { return address; }
  public Instant getCreatedAt() { return createdAt; }
}