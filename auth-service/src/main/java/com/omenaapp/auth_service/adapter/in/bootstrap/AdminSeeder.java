package com.omenaapp.auth_service.adapter.in.bootstrap;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.omenaapp.auth_service.application.port.out.PasswordHasherPort;
import com.omenaapp.auth_service.application.port.out.UserRepositoryPort;
import com.omenaapp.auth_service.domain.User;

@Component
public class AdminSeeder implements CommandLineRunner {

  private final UserRepositoryPort users;
  private final PasswordHasherPort hasher;

  private final boolean enabled;
  private final String email;
  private final String username;
  private final String password;

  public AdminSeeder(
      UserRepositoryPort users,
      PasswordHasherPort hasher,
      @Value("${seed.admin.enabled:false}") boolean enabled,
      @Value("${seed.admin.email:}") String email,
      @Value("${seed.admin.username:}") String username,
      @Value("${seed.admin.password:}") String password
  ) {
    this.users = users;
    this.hasher = hasher;
    this.enabled = enabled;
    this.email = email;
    this.username = username;
    this.password = password;
  }

  @Override
  @Transactional
  public void run(String... args) {
    if (!enabled) return;

    String em = normalizeEmail(email);
    String un = username == null ? "" : username.trim();
    String pw = password == null ? "" : password;

    if (em.isEmpty() || un.isEmpty() || pw.isEmpty()) {
      System.out.println("[SEED_ADMIN] enabled but missing email/username/password -> skip");
      return;
    }
    if (pw.length() < 8) {
      System.out.println("[SEED_ADMIN] password too short -> skip");
      return;
    }

    // Idempotent: si déjà présent par email ou username, on ne recrée pas.
    if (users.findByEmail(em).isPresent()) {
      System.out.println("[SEED_ADMIN] admin email already exists -> skip");
      return;
    }
    if (users.findByUsername(un).isPresent()) {
      System.out.println("[SEED_ADMIN] admin username already exists -> skip");
      return;
    }

    UUID id = UUID.randomUUID();
    String hash = hasher.hash(pw);

    users.save(new User(id, em, un, hash, Instant.now(), true));
    System.out.println("[SEED_ADMIN] admin created: " + em);
  }

  private static String normalizeEmail(String e) {
    return e == null ? "" : e.trim().toLowerCase();
  }
}
