package com.omenaapp.auth_service.adapter.in.web;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.omenaapp.auth_service.application.port.in.AdminCreateUserUseCase;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/admin/users")
public class AdminUsersController {

  private final AdminCreateUserUseCase createUser;

  public AdminUsersController(AdminCreateUserUseCase createUser) {
    this.createUser = createUser;
  }

  @PostMapping
  public ResponseEntity<CreateUserResponse> create(@Valid @RequestBody CreateUserRequest req) {
    UUID id = createUser.create(new AdminCreateUserUseCase.Command(req.email, req.username, req.password));
    return ResponseEntity.ok(new CreateUserResponse(id));
  }

  public static class CreateUserRequest {
    @NotBlank @Email public String email;
    @NotBlank public String username;
    @NotBlank public String password;
  }

  public record CreateUserResponse(UUID userId) {}
}