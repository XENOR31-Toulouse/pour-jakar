package com.omenaapp.auth_service.application.port;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.omenaapp.auth_service.application.port.in.LoginUseCase;
import com.omenaapp.auth_service.application.port.in.RegisterUserUseCase;
import com.omenaapp.auth_service.application.port.out.PasswordHasherPort;
import com.omenaapp.auth_service.application.port.out.TokenIssuerPort;
import com.omenaapp.auth_service.application.port.out.UserRepositoryPort;
import com.omenaapp.auth_service.domain.User;


@Service
public class AuthService implements RegisterUserUseCase, LoginUseCase {

  private final UserRepositoryPort users;
  private final PasswordHasherPort hasher;
  private final TokenIssuerPort tokenIssuer;

  public AuthService(UserRepositoryPort users, PasswordHasherPort hasher, TokenIssuerPort tokenIssuer) {
    this.users = users;
    this.hasher = hasher;
    this.tokenIssuer = tokenIssuer;
  }

  @Override
  @Transactional
  public UUID register(RegisterUserUseCase.Command cmd) {
    String email = normalizeEmail(cmd.email());
    String username = cmd.username() == null ? "" : cmd.username().trim();
    String password = cmd.password() == null ? "" : cmd.password();

    if (!email.contains("@")) throw new IllegalArgumentException("INVALID_EMAIL");
    if (username.length() < 3) throw new IllegalArgumentException("USERNAME_TOO_SHORT");
    if (password.length() < 8) throw new IllegalArgumentException("PASSWORD_TOO_SHORT");

    if (users.findByEmail(email).isPresent()) throw new IllegalStateException("EMAIL_ALREADY_USED");
    if (users.findByUsername(username).isPresent()) throw new IllegalStateException("USERNAME_ALREADY_USED");

    UUID id = UUID.randomUUID();
    String hash = hasher.hash(password);

    users.save(new User(id, email, username, hash, Instant.now()));
    return id;
  }

  @Override
  public LoginUseCase.Result login(LoginUseCase.Command cmd) {
    String identifier = cmd.identifier() == null ? "" : cmd.identifier().trim();
    String password = cmd.password() == null ? "" : cmd.password();

    boolean isEmail = identifier.contains("@");
    var userOpt = isEmail
        ? users.findByEmail(normalizeEmail(identifier))
        : users.findByUsername(identifier);

    var user = userOpt.orElseThrow(() -> new IllegalArgumentException("INVALID_CREDENTIALS"));

    if (!hasher.matches(password, user.passwordHash()))
      throw new IllegalArgumentException("INVALID_CREDENTIALS");

    String token = tokenIssuer.issueAccessToken(
        user.id(),
        Map.of("email", user.email(), "username", user.username())
    );

    return new LoginUseCase.Result(token);
  }

  private static String normalizeEmail(String email) {
    return email == null ? "" : email.trim().toLowerCase();
  }
}