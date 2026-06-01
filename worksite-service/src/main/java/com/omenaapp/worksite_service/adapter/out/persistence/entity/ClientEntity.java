package com.omenaapp.worksite_service.adapter.out.persistence.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="clients")
public class ClientEntity {
  @Id
  private UUID id;

  @Column(nullable=false)
  private String name;

  @Column(nullable=false, unique=true)
  private String email;

  @Column(nullable=false)
  private String phoneNumber;

  private String address;

  protected ClientEntity() {}

  public ClientEntity(UUID id, String name, String email, String phoneNumber, String address) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.phoneNumber = phoneNumber;
    this.address = address;
  }

  public UUID getId() { return id; }
  public String getName() { return name; }
  public String getEmail() { return email; }
  public String getPhoneNumber() { return phoneNumber; }
  public String getAddress() { return address; }
}