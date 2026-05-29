package com.omenaapp.worksite_service.adapter.out.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.omenaapp.worksite_service.adapter.out.persistence.entity.ClientEntity;

public interface ClientRepo extends JpaRepository<ClientEntity, UUID> {}