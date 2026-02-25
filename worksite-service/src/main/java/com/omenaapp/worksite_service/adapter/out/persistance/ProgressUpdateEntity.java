package com.omenaapp.worksite_service.adapter.out.persistance;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name="progress_updates", indexes = {
    @Index(name="ix_progress_worksite", columnList="worksiteId"),
    @Index(name="ix_progress_user", columnList="userId")
})
public class ProgressUpdateEntity {

  @Id
  private UUID id;

  @Column(nullable=false)
  private UUID worksiteId;

  @Column(nullable=false)
  private UUID userId;

  @Column(nullable=false, length=1000)
  private String note;

  // optionnel (peut être null)
  private Integer percent;

  @Column(nullable=false)
  private Instant createdAt;

  protected ProgressUpdateEntity() {}

  public ProgressUpdateEntity(UUID id, UUID worksiteId, UUID userId, String note, Integer percent, Instant createdAt) {
    this.id = id;
    this.worksiteId = worksiteId;
    this.userId = userId;
    this.note = note;
    this.percent = percent;
    this.createdAt = createdAt;
  }

  public UUID getId() { return id; }
  public UUID getWorksiteId() { return worksiteId; }
  public UUID getUserId() { return userId; }
  public String getNote() { return note; }
  public Integer getPercent() { return percent; }
  public Instant getCreatedAt() { return createdAt; }
}
