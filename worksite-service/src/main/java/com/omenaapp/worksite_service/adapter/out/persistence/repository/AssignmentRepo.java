package com.omenaapp.worksite_service.adapter.out.persistence.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import com.omenaapp.worksite_service.adapter.out.persistence.entity.AssignmentEntity;

public interface AssignmentRepo extends JpaRepository<AssignmentEntity, UUID> {

  List<AssignmentEntity> findByUserId(UUID userId);

  List<AssignmentEntity> findByWorksiteId(UUID worksiteId);

  boolean existsByWorksiteIdAndUserId(UUID worksiteId, UUID userId);

  @Modifying
  @Transactional
  void deleteByWorksiteIdAndUserId(UUID worksiteId, UUID userId);
}
