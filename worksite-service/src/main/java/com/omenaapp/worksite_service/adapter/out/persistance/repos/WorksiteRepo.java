package com.omenaapp.worksite_service.adapter.out.persistance.repos;




import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.omenaapp.worksite_service.adapter.out.persistance.WorksiteEntity;


public interface WorksiteRepo extends JpaRepository<WorksiteEntity, UUID> {}