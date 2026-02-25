package com.omenaapp.worksite_service.adapter.out.persistance;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
  name="assignments",
  uniqueConstraints = @UniqueConstraint(name="uk_assignment", columnNames={"worksiteId","userId"})
)
public class AssignmentEntity {
  @Id
  private UUID id;

  @Column(nullable=false)
  private UUID worksiteId;

  @Column(nullable=false)
  private UUID userId;

  @Column(nullable=false)
  private Instant assignedAt;

  protected AssignmentEntity() {}

  public AssignmentEntity(UUID id, UUID worksiteId, UUID userId, Instant assignedAt) {
    this.id = id;
    this.worksiteId = worksiteId;
    this.userId = userId;
    this.assignedAt = assignedAt;
  }

  public UUID getId() { return id; }
  public UUID getWorksiteId() { return worksiteId; }
  public UUID getUserId() { return userId; }
  public Instant getAssignedAt() { return assignedAt; }
}