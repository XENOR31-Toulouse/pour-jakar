package com.omenaapp.worksite_service.adapter.out.persistence.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.omenaapp.worksite_service.adapter.out.persistence.entity.WorkEventEntity;

public interface WorkEventRepo extends JpaRepository<WorkEventEntity, UUID> {
  List<WorkEventEntity> findByWorksiteId(UUID worksiteId);
  List<WorkEventEntity> findByWorksiteIdAndUserId(UUID worksiteId, UUID userId);
}
