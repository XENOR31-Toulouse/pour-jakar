package com.omenaapp.worksite_service.domain;


import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.omenaapp.worksite_service.adapter.out.persistance.AssignmentEntity;


public interface AssignmentRepo extends JpaRepository<AssignmentEntity, UUID> {
  List<AssignmentEntity> findByUserId(UUID userId);
  List<AssignmentEntity> findByWorksiteId(UUID worksiteId);
  void deleteByWorksiteIdAndUserId(UUID worksiteId, UUID userId);
}