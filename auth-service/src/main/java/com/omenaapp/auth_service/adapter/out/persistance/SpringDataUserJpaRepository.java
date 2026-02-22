package com.omenaapp.auth_service.adapter.out.persistance;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataUserJpaRepository extends JpaRepository<UserEntity, UUID> {
  Optional<UserEntity> findByEmail(String email);
  Optional<UserEntity> findByUsername(String username);

  Optional<UserEntity> findById(UUID id);

  List<UserEntity> findByIsAdminFalse();
}
