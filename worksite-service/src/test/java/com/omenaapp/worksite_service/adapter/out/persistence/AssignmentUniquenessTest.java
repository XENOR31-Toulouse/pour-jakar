package com.omenaapp.worksite_service.adapter.out.persistence;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.omenaapp.worksite_service.adapter.out.persistence.adapter.AssignmentPersistenceAdapter;
import com.omenaapp.worksite_service.adapter.out.persistence.adapter.WorksitePersistenceAdapter;
import com.omenaapp.worksite_service.domain.model.Assignment;
import com.omenaapp.worksite_service.domain.model.Worksite;

@Testcontainers
@SpringBootTest
class AssignmentUniquenessTest {

  @Container
  static final PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:16-alpine")
          .withDatabaseName("testdb")
          .withUsername("test")
          .withPassword("test");

  @DynamicPropertySource
  static void datasourceProps(DynamicPropertyRegistry r) {
    r.add("spring.datasource.url", postgres::getJdbcUrl);
    r.add("spring.datasource.username", postgres::getUsername);
    r.add("spring.datasource.password", postgres::getPassword);

    // important for tests: build schema from entities
    r.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    r.add("spring.jpa.properties.hibernate.dialect",
        () -> "org.hibernate.dialect.PostgreSQLDialect");
  }

  @Autowired WorksitePersistenceAdapter worksites;
  @Autowired AssignmentPersistenceAdapter assignments;

  @Test
  void cannotInsertDuplicateAssignment_forSameWorksiteAndUser() {
    UUID worksiteId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    // Create a worksite first (required by your service logic)
    worksites.save(new Worksite(worksiteId, "test2", "3 rue de la place", Instant.now()));

    // First insert OK
    assignments.save(new Assignment(UUID.randomUUID(), worksiteId, userId, Instant.now()));

    // Second insert with same (worksiteId, userId) should fail if UNIQUE constraint exists
    assertThrows(DataIntegrityViolationException.class, () ->
        assignments.save(new Assignment(UUID.randomUUID(), worksiteId, userId, Instant.now()))
    );
  }
}