package com.omenaapp.worksite_service.adapter.out.persistance.repos;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.omenaapp.worksite_service.adapter.out.persistance.ProgressUpdateEntity;

public interface ProgressUpdateRepo extends JpaRepository<ProgressUpdateEntity, UUID> {
  List<ProgressUpdateEntity> findByWorksiteId(UUID worksiteId);
  List<ProgressUpdateEntity> findByWorksiteIdAndUserId(UUID worksiteId, UUID userId);
}