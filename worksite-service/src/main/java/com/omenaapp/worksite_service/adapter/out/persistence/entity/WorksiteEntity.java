package com.omenaapp.worksite_service.adapter.out.persistence.entity;



import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

  @ManyToOne
  @JoinColumn(name = "client_id")
  private ClientEntity client;

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
  public ClientEntity getClient() { return client; }

  public void setId(UUID id) { this.id = id; }
  public void setName(String name) { this.name = name; }
  public void setAddress(String address) { this.address = address; }
  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
  public void setClient(ClientEntity client) { this.client = client; }
}

