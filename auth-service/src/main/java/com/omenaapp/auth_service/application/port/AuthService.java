package com.omenaapp.auth_service.application.port;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.omenaapp.auth_service.application.port.in.AdminCreateUserUseCase;
import com.omenaapp.auth_service.application.port.in.LoginUseCase;
import com.omenaapp.auth_service.application.port.in.LogoutUseCase;
import com.omenaapp.auth_service.application.port.in.RefreshUseCase;
import com.omenaapp.auth_service.application.port.in.RegisterUserUseCase;
import com.omenaapp.auth_service.application.port.in.RequestPasswordResetUseCase;
import com.omenaapp.auth_service.application.port.in.ResetPasswordUseCase;
import com.omenaapp.auth_service.application.port.out.PasswordHasherPort;
import com.omenaapp.auth_service.application.port.out.PasswordResetNotifierPort;
import com.omenaapp.auth_service.application.port.out.PasswordResetTokenRepositoryPort;
import com.omenaapp.auth_service.application.port.out.RefreshTokenRepositoryPort;
import com.omenaapp.auth_service.application.port.out.TokenHasherPort;
import com.omenaapp.auth_service.application.port.out.TokenIssuerPort;
import com.omenaapp.auth_service.application.port.out.UserRepositoryPort;
import com.omenaapp.auth_service.domain.User;

@Service
public class AuthService implements
        RegisterUserUseCase,
        LoginUseCase,
        RefreshUseCase,
        LogoutUseCase,
        RequestPasswordResetUseCase,
        ResetPasswordUseCase,
        AdminCreateUserUseCase {

    private final UserRepositoryPort users;
    private final PasswordHasherPort hasher;
    private final TokenIssuerPort tokenIssuer;

    private final RefreshTokenRepositoryPort refreshRepo;
    private final TokenHasherPort tokenHasher;

    private final PasswordResetTokenRepositoryPort resetTokens;
    private final PasswordResetNotifierPort resetNotifier;
    

    private final long refreshTtlSeconds;
    private final long resetTtlSeconds;

    public AuthService(
            UserRepositoryPort users,
            PasswordHasherPort hasher,
            TokenIssuerPort tokenIssuer,
            RefreshTokenRepositoryPort refreshRepo,
            TokenHasherPort tokenHasher,
            PasswordResetTokenRepositoryPort resetTokens,
            PasswordResetNotifierPort resetNotifier,
            @Value("${security-refresh.ttl-seconds:604800}") long refreshTtlSeconds,
            @Value("${security-reset.ttl-seconds:900}") long resetTtlSeconds
    ) {
        this.users = users;
        this.hasher = hasher;
        this.tokenIssuer = tokenIssuer;

        this.refreshRepo = refreshRepo;
        this.tokenHasher = tokenHasher;

        this.resetTokens = resetTokens;
        this.resetNotifier = resetNotifier;

        this.refreshTtlSeconds = refreshTtlSeconds;
        this.resetTtlSeconds = resetTtlSeconds;
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

        // ✅ par défaut : pas admin
        users.save(new User(id, email, username, hash, Instant.now(), false));
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

        refreshRepo.revokeToken(existing.id(), now);

        User user = users.findById(existing.userId())
                .orElseThrow(() -> new IllegalArgumentException("USER_NOT_FOUND"));

        var issued = issueTokens(user);
        return new RefreshUseCase.Result(issued.accessToken(), issued.refreshToken());
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

    // ---- Reset Password ----
    @Override
    @Transactional
    public void request(RequestPasswordResetUseCase.Command cmd) {
        String email = normalizeEmail(cmd.email());
        if (email.isEmpty()) {
            return;
        }

        var userOpt = users.findByEmail(email);
        if (userOpt.isEmpty()) {
            return; // anti-enumeration
        }
        var user = userOpt.get();

        String rawToken = UUID.randomUUID().toString();
        String tokenHash = tokenHasher.sha256(rawToken);

        Instant now = Instant.now();
        Instant exp = now.plusSeconds(resetTtlSeconds);

        resetTokens.save(new PasswordResetTokenRepositoryPort.ResetTokenRecord(
                UUID.randomUUID(),
                user.id(),
                tokenHash,
                exp,
                null,
                now
        ));

        // ✅ IMPORTANT: ne pas faire échouer l'API si l'email échoue
        try {
            resetNotifier.sendResetLink(user.email(), rawToken);
        } catch (Exception ex) {
            System.out.println("[RESET-PASSWORD] Email failed: " + ex.getMessage());
        }
    }

    @Override
    @Transactional
    public UUID create(AdminCreateUserUseCase.Command cmd) {
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

        // ✅ user normal
        users.save(new com.omenaapp.auth_service.domain.User(id, email, username, hash, java.time.Instant.now(), false));
        return id;
    }

    @Override
    @Transactional
    public void reset(ResetPasswordUseCase.Command cmd) {
        String raw = cmd.token() == null ? "" : cmd.token().trim();
        String newPwd = cmd.newPassword() == null ? "" : cmd.newPassword();

        if (raw.isEmpty()) {
            throw new IllegalArgumentException("RESET_TOKEN_REQUIRED");
        }
        if (newPwd.length() < 8) {
            throw new IllegalArgumentException("PASSWORD_TOO_SHORT");
        }

        Instant now = Instant.now();
        String hash = tokenHasher.sha256(raw);

        var token = resetTokens.findValidByTokenHash(hash, now)
                .orElseThrow(() -> new IllegalArgumentException("RESET_TOKEN_INVALID"));

        User user = users.findById(token.userId())
                .orElseThrow(() -> new IllegalArgumentException("USER_NOT_FOUND"));

        String newHash = hasher.hash(newPwd);

        // ✅ on garde email/username/isAdmin identiques, on change seulement passwordHash
        users.save(new User(
                user.id(),
                user.email(),
                user.username(),
                newHash,
                user.createdAt(),
                user.isAdmin()
        ));

        resetTokens.markUsed(token.id(), now);
    }

    // ---- Helpers ----
    private LoginUseCase.Result issueTokens(User user) {
        String role = user.isAdmin() ? "ADMIN" : "USER"; // ✅ string

        String access = tokenIssuer.issueAccessToken(
                user.id(),
                Map.of(
                        "email", user.email(),
                        "username", user.username(),
                        "role", role
                )
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
