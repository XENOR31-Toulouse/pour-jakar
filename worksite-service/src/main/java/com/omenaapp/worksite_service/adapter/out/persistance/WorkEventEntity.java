package com.omenaapp.worksite_service.adapter.out.persistance;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name="work_events", indexes = {
    @Index(name="ix_work_events_worksite", columnList="worksiteId"),
    @Index(name="ix_work_events_user", columnList="userId")
})
public class WorkEventEntity {

  @Id
  private UUID id;

  @Column(nullable=false)
  private UUID worksiteId;

  @Column(nullable=false)
  private UUID userId;

  @Enumerated(EnumType.STRING)
  @Column(nullable=false)
  private WorkEventType type;

  @Column(nullable=false)
  private Instant occurredAt;

  protected WorkEventEntity() {}

  public WorkEventEntity(UUID id, UUID worksiteId, UUID userId, WorkEventType type, Instant occurredAt) {
    this.id = id;
    this.worksiteId = worksiteId;
    this.userId = userId;
    this.type = type;
    this.occurredAt = occurredAt;
  }

  public UUID getId() { return id; }
  public UUID getWorksiteId() { return worksiteId; }
  public UUID getUserId() { return userId; }
  public WorkEventType getType() { return type; }
  public Instant getOccurredAt() { return occurredAt; }
}