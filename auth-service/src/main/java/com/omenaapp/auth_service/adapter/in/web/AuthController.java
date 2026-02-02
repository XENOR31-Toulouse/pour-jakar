package com.omenaapp.auth_service.adapter.in.web;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.omenaapp.auth_service.application.port.in.LoginUseCase;
import com.omenaapp.auth_service.application.port.in.LogoutUseCase;
import com.omenaapp.auth_service.application.port.in.RefreshUseCase;
import com.omenaapp.auth_service.application.port.in.RegisterUserUseCase;
import com.omenaapp.auth_service.application.port.in.RequestPasswordResetUseCase;
import com.omenaapp.auth_service.application.port.in.ResetPasswordUseCase;

import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final RegisterUserUseCase register;
  private final LoginUseCase login;
  private final RefreshUseCase refresh;
  private final LogoutUseCase logout;
  private final RequestPasswordResetUseCase requestReset;
  private final ResetPasswordUseCase resetPassword;

  public AuthController(RegisterUserUseCase register, LoginUseCase login, RefreshUseCase refresh, LogoutUseCase logout,
      RequestPasswordResetUseCase requestReset, ResetPasswordUseCase resetPassword) {
    this.register = register;
    this.login = login;
    this.refresh = refresh;
    this.logout = logout;
    this.requestReset = requestReset;
    this.resetPassword = resetPassword;
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
    UUID id = register.register(new RegisterUserUseCase.Command(req.email, req.username, req.password));
    return ResponseEntity.ok(new IdResponse(id));
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest req) {
    var result = login.login(new LoginUseCase.Command(req.identifier, req.password));
    return ResponseEntity.ok(result); // { "accessToken": "..." }
  }

  @PostMapping("/refresh")
public ResponseEntity<?> refresh(@RequestBody RefreshRequest req) {
  var result = refresh.refresh(new RefreshUseCase.Command(req.refreshToken));
  return ResponseEntity.ok(result);
}

@PostMapping("/logout")
public ResponseEntity<?> logout(@RequestBody RefreshRequest req) {
  logout.logout(new LogoutUseCase.Command(req.refreshToken));
  return ResponseEntity.ok().build();
}

@PostMapping("/password/reset-request")
  public ResponseEntity<?> requestReset(@RequestBody ResetRequestEmail req) {
    requestReset.request(new RequestPasswordResetUseCase.Command(req.email));
    return ResponseEntity.ok().build();
  }

  @PostMapping("/password/reset")
  public ResponseEntity<?> reset(@RequestBody ResetPasswordBody req) {
    resetPassword.reset(new ResetPasswordUseCase.Command(req.token, req.newPassword));
    return ResponseEntity.ok().build();
  }

public static class RefreshRequest {
  @NotBlank public String refreshToken;
}


  public static class RegisterRequest {
    @NotBlank public String email;
    @NotBlank public String username;
    @NotBlank public String password;
  }

  public static class LoginRequest {
    @NotBlank public String identifier;
    @NotBlank public String password;
  }

  public static class ResetRequestEmail {
    @NotBlank public String email;
  }

  public static class ResetPasswordBody {
    @NotBlank public String token;
    @NotBlank public String newPassword;
  }

  public record IdResponse(UUID userId) {}
}