package com.omenaapp.worksite_service.domain;




import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.omenaapp.worksite_service.adapter.out.persistance.WorksiteEntity;


public interface WorksiteRepo extends JpaRepository<WorksiteEntity, UUID> {}