package com.omenaapp.auth_service.application.port;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.omenaapp.auth_service.application.port.in.LoginUseCase;
import com.omenaapp.auth_service.application.port.in.LogoutUseCase;
import com.omenaapp.auth_service.application.port.in.RefreshUseCase;
import com.omenaapp.auth_service.application.port.in.RegisterUserUseCase;
import com.omenaapp.auth_service.application.port.out.PasswordHasherPort;
import com.omenaapp.auth_service.application.port.out.RefreshTokenRepositoryPort;
import com.omenaapp.auth_service.application.port.out.TokenHasherPort;
import com.omenaapp.auth_service.application.port.out.TokenIssuerPort;
import com.omenaapp.auth_service.application.port.out.UserRepositoryPort;
import com.omenaapp.auth_service.domain.User;

@Service
public class AuthService implements RegisterUserUseCase, LoginUseCase, RefreshUseCase, LogoutUseCase {

    private final UserRepositoryPort users;
    private final PasswordHasherPort hasher;
    private final TokenIssuerPort tokenIssuer;

    private final RefreshTokenRepositoryPort refreshRepo;
    private final TokenHasherPort tokenHasher;

    private final long refreshTtlSeconds;

    public AuthService(
            UserRepositoryPort users,
            PasswordHasherPort hasher,
            TokenIssuerPort tokenIssuer,
            RefreshTokenRepositoryPort refreshRepo,
            TokenHasherPort tokenHasher,
            @Value("${security-refresh.ttl-seconds:604800}") long refreshTtlSeconds
    ) {
        this.users = users;
        this.hasher = hasher;
        this.tokenIssuer = tokenIssuer;
        this.refreshRepo = refreshRepo;
        this.tokenHasher = tokenHasher;
        this.refreshTtlSeconds = refreshTtlSeconds;
    }

    @Override
    @Transactional
    public UUID register(RegisterUserUseCase.Command cmd) {
        String email = normalizeEmail(cmd.email());
        String username = cmd.username() == null ? "" : cmd.username().trim();
        String password = cmd.password() == null ? "" : cmd.password();

        if (!email.contains("@")) {
            throw new IllegalArgumentException("INVALID_EMAIL");
        }
        if (username.length() < 3) {
            throw new IllegalArgumentException("USERNAME_TOO_SHORT");
        }
        if (password.length() < 8) {
            throw new IllegalArgumentException("PASSWORD_TOO_SHORT");
        }

        if (users.findByEmail(email).isPresent()) {
            throw new IllegalStateException("EMAIL_ALREADY_USED");
        }
        if (users.findByUsername(username).isPresent()) {
            throw new IllegalStateException("USERNAME_ALREADY_USED");
        }

        UUID id = UUID.randomUUID();
        String hash = hasher.hash(password);

        users.save(new User(id, email, username, hash, Instant.now()));
        return id;
    }

    @Override
    @Transactional
    public LoginUseCase.Result login(LoginUseCase.Command cmd) {
        String identifier = cmd.identifier() == null ? "" : cmd.identifier().trim();
        String password = cmd.password() == null ? "" : cmd.password();

        boolean isEmail = identifier.contains("@");
        var userOpt = isEmail
                ? users.findByEmail(normalizeEmail(identifier))
                : users.findByUsername(identifier);

        var user = userOpt.orElseThrow(() -> new IllegalArgumentException("INVALID_CREDENTIALS"));

        if (!hasher.matches(password, user.passwordHash())) {
            throw new IllegalArgumentException("INVALID_CREDENTIALS");
        }

        return issueTokens(user);
    }

    @Override
    @Transactional
    public RefreshUseCase.Result refresh(RefreshUseCase.Command cmd) {
        String rawRefresh = cmd.refreshToken() == null ? "" : cmd.refreshToken().trim();
        if (rawRefresh.isEmpty()) {
            throw new IllegalArgumentException("REFRESH_REQUIRED");
        }

        Instant now = Instant.now();
        String hash = tokenHasher.sha256(rawRefresh);

        var existing = refreshRepo.findValidByTokenHash(hash, now)
                .orElseThrow(() -> new IllegalArgumentException("REFRESH_INVALID"));

        // rotation: révoquer l’ancien
        refreshRepo.revokeToken(existing.id(), now);

        // émettre un nouvel access + refresh
        // on a juste userId dans refresh token, donc on recharge le user pour claims
        User user = users.findById(existing.userId())
                .orElseThrow(() -> new IllegalArgumentException("USER_NOT_FOUND"));

        var issued = issueTokens(user);
        return new RefreshUseCase.Result(issued.accessToken(), issued.refreshToken());
        // -> On ajoute plutôt un port findById, plus propre.
        // throw new RuntimeException("IMPLEMENT_FIND_BY_ID");
    }

    @Override
    @Transactional
    public void logout(LogoutUseCase.Command cmd) {
        String rawRefresh = cmd.refreshToken() == null ? "" : cmd.refreshToken().trim();
        if (rawRefresh.isEmpty()) {
            return;
        }

        Instant now = Instant.now();
        String hash = tokenHasher.sha256(rawRefresh);

        refreshRepo.findValidByTokenHash(hash, now)
                .ifPresent(rt -> refreshRepo.revokeToken(rt.id(), now));
    }

    private LoginUseCase.Result issueTokens(User user) {
        String access = tokenIssuer.issueAccessToken(
                user.id(),
                Map.of("email", user.email(), "username", user.username())
        );

        String refreshRaw = UUID.randomUUID().toString();
        String refreshHash = tokenHasher.sha256(refreshRaw);
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(refreshTtlSeconds);

        refreshRepo.save(new RefreshTokenRepositoryPort.RefreshTokenRecord(
                UUID.randomUUID(),
                user.id(),
                refreshHash,
                exp,
                null,
                now
        ));

        return new LoginUseCase.Result(access, refreshRaw);
    }

    private static String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }
}
