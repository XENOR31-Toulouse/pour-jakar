package com.omenaapp.auth_service.adapter.in.web;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.omenaapp.auth_service.domain.port.out.PasswordHasherPort;
import com.omenaapp.auth_service.domain.port.out.UserRepositoryPort;
import com.omenaapp.auth_service.domain.model.User;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/admin/users")
public class AdminUsersController {

  private final UserRepositoryPort users;
  private final PasswordHasherPort hasher;

  public AdminUsersController(UserRepositoryPort users, PasswordHasherPort hasher) {
    this.users = users;
    this.hasher = hasher;
  }

  // DTOs
  public record EmployeeDto(UUID id, String email, String username, Instant createdAt) {}
  public record CreateUserResponse(UUID userId) {}

  public static class CreateUserRequest {
    @NotBlank @Email public String email;
    @NotBlank public String username;
    @NotBlank public String password;
  }

  // ---- LIST employees (non-admin) ----
  @GetMapping("/employees")
  public List<EmployeeDto> listEmployees() {
    return users.findEmployees().stream()
      .map(u -> new EmployeeDto(u.id(), u.email(), u.username(), u.createdAt()))
      .toList();

      //exeptions
      // - if you want to add pagination, you can add query params like ?page=1&size=20
      // - if you want to add filtering/sorting, you can add query params like ?
  }

  // ---- CREATE employee (non-admin) ----
  @PostMapping("/employees")
  public ResponseEntity<CreateUserResponse> createEmployee(@Valid @RequestBody CreateUserRequest req) {
    String email = normalizeEmail(req.email);
    String username = req.username.trim();
    String password = req.password;

    if (username.length() < 3) throw new IllegalArgumentException("USERNAME_TOO_SHORT");
    if (password.length() < 8) throw new IllegalArgumentException("PASSWORD_TOO_SHORT");

    if (users.findByEmail(email).isPresent()) throw new IllegalStateException("EMAIL_ALREADY_USED");
    if (users.findByUsername(username).isPresent()) throw new IllegalStateException("USERNAME_ALREADY_USED");

    UUID id = UUID.randomUUID();
    String hash = hasher.hash(password);

    // ✅ employee = isAdmin false
    users.save(new User(id, email, username, hash, Instant.now(), false));

    return ResponseEntity.ok(new CreateUserResponse(id));
  }

  // ---- DELETE employee (hard delete) ----
  @DeleteMapping("/employees/{id}")
  public ResponseEntity<?> deleteEmployee(@PathVariable UUID id) {
    var userOpt = users.findById(id);
    if (userOpt.isEmpty()) return ResponseEntity.notFound().build();

    if (userOpt.get().isAdmin()) {
      throw new IllegalArgumentException("CANNOT_DELETE_ADMIN");
    }

    users.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  // (Optional) keep your old endpoint for backward compatibility:
  // POST /admin/users  -> same as create employee
  @PostMapping
  public ResponseEntity<CreateUserResponse> create(@Valid @RequestBody CreateUserRequest req) {
    return createEmployee(req);
  }

  private static String normalizeEmail(String email) {
    return email == null ? "" : email.trim().toLowerCase();
  }
}