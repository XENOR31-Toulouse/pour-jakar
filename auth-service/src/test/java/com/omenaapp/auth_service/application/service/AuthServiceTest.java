package com.omenaapp.auth_service.application.service;

import com.omenaapp.auth_service.application.port.in.*;
import com.omenaapp.auth_service.domain.model.User;
import com.omenaapp.auth_service.domain.port.out.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private UserRepositoryPort users;
    private PasswordHasherPort hasher;
    private TokenIssuerPort tokenIssuer;
    private RefreshTokenRepositoryPort refreshRepo;
    private TokenHasherPort tokenHasher;
    private PasswordResetTokenRepositoryPort resetTokens;
    private PasswordResetNotifierPort resetNotifier;

    private AuthService service;

    @BeforeEach
    void setUp() {
        users = mock(UserRepositoryPort.class);
        hasher = mock(PasswordHasherPort.class);
        tokenIssuer = mock(TokenIssuerPort.class);
        refreshRepo = mock(RefreshTokenRepositoryPort.class);
        tokenHasher = mock(TokenHasherPort.class);
        resetTokens = mock(PasswordResetTokenRepositoryPort.class);
        resetNotifier = mock(PasswordResetNotifierPort.class);

        // deterministic-ish: return a stable hash prefix so we can assert "some hash" was produced
        when(tokenHasher.sha256(anyString())).thenAnswer(inv -> "sha256:" + inv.getArgument(0));
        when(tokenIssuer.issueAccessToken(any(UUID.class), anyMap())).thenReturn("ACCESS");
        when(hasher.hash(anyString())).thenAnswer(inv -> "HASH:" + inv.getArgument(0));

        service = new AuthService(
                users,
                hasher,
                tokenIssuer,
                refreshRepo,
                tokenHasher,
                resetTokens,
                resetNotifier,
                3600,   // refresh ttl
                900     // reset ttl
        );
    }

    @Test
    void register_invalidEmail_throws() {
        var cmd = new RegisterUserUseCase.Command("not-an-email", "john", "password123");
        var ex = assertThrows(IllegalArgumentException.class, () -> service.register(cmd));
        assertEquals("INVALID_EMAIL", ex.getMessage());
        verifyNoInteractions(users);
    }

    @Test
    void register_valid_savesUser_andReturnsId() {
        when(users.findByEmail("a@b.com")).thenReturn(Optional.empty());
        when(users.findByUsername("john")).thenReturn(Optional.empty());

        UUID id = service.register(new RegisterUserUseCase.Command("A@B.com", " john ", "password123"));
        assertNotNull(id);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(users).save(captor.capture());
        User saved = captor.getValue();

        assertEquals(id, saved.id());
        assertEquals("a@b.com", saved.email());
        assertEquals("john", saved.username());
        assertEquals("HASH:password123", saved.passwordHash());
        assertFalse(saved.isAdmin());

        verify(users).findByEmail("a@b.com");
        verify(users).findByUsername("john");
    }

    @Test
    void login_withUsername_invalidCredentials_throws() {
        when(users.findByUsername("john")).thenReturn(Optional.empty());
        var ex = assertThrows(IllegalArgumentException.class,
                () -> service.login(new LoginUseCase.Command("john", "password123")));
        assertEquals("INVALID_CREDENTIALS", ex.getMessage());
    }

    @Test
    void login_success_issuesAccessAndRefreshTokens() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "a@b.com", "john", "HASH:password123", Instant.now(), false);

        when(users.findByUsername("john")).thenReturn(Optional.of(user));
        when(hasher.matches("password123", "HASH:password123")).thenReturn(true);

        LoginUseCase.Result result = service.login(new LoginUseCase.Command("john", "password123"));

        assertEquals("ACCESS", result.accessToken());
        assertNotNull(result.refreshToken());
        assertFalse(result.refreshToken().isBlank());

        // access token claims include role
        verify(tokenIssuer).issueAccessToken(eq(userId), argThat((Map<String, Object> claims) ->
                "a@b.com".equals(claims.get("email"))
                        && "john".equals(claims.get("username"))
                        && "USER".equals(claims.get("role"))
        ));

        // refresh token persisted
        verify(refreshRepo).save(any(RefreshTokenRepositoryPort.RefreshTokenRecord.class));
    }

    @Test
    void refresh_invalidMissingToken_throws() {
        var ex = assertThrows(IllegalArgumentException.class,
                () -> service.refresh(new RefreshUseCase.Command(" ")));
        assertEquals("REFRESH_REQUIRED", ex.getMessage());
    }

    @Test
    void refresh_valid_revokesOld_andIssuesNew() {
        UUID userId = UUID.randomUUID();
        UUID rtId = UUID.randomUUID();
        Instant now = Instant.now();

        String rawRefresh = "RAW_REFRESH";
        String hashed = "sha256:" + rawRefresh;

        when(refreshRepo.findValidByTokenHash(eq(hashed), any(Instant.class)))
                .thenReturn(Optional.of(new RefreshTokenRepositoryPort.RefreshTokenRecord(
                        rtId, userId, hashed, now.plusSeconds(10), null, now.minusSeconds(10)
                )));

        when(users.findById(userId)).thenReturn(Optional.of(
                new User(userId, "a@b.com", "john", "HASH:x", Instant.now(), false)
        ));

        RefreshUseCase.Result result = service.refresh(new RefreshUseCase.Command(rawRefresh));
        assertEquals("ACCESS", result.accessToken());
        assertNotNull(result.refreshToken());

        verify(refreshRepo).revokeToken(eq(rtId), any(Instant.class));
        verify(refreshRepo).save(any(RefreshTokenRepositoryPort.RefreshTokenRecord.class));
    }

    @Test
    void logout_emptyToken_isNoop() {
        assertDoesNotThrow(() -> service.logout(new LogoutUseCase.Command("  ")));
        verifyNoInteractions(refreshRepo);
    }

    @Test
    void requestPasswordReset_unknownEmail_doesNothing() {
        when(users.findByEmail("a@b.com")).thenReturn(Optional.empty());

        service.request(new RequestPasswordResetUseCase.Command("a@b.com"));

        verify(users).findByEmail("a@b.com");
        verifyNoInteractions(resetTokens, resetNotifier);
    }

    @Test
    void requestPasswordReset_knownEmail_savesToken_andSendsLink() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "a@b.com", "john", "HASH:x", Instant.now(), false);
        when(users.findByEmail("a@b.com")).thenReturn(Optional.of(user));

        service.request(new RequestPasswordResetUseCase.Command("a@b.com"));

        verify(resetTokens).save(any(PasswordResetTokenRepositoryPort.ResetTokenRecord.class));
        verify(resetNotifier).sendResetLink(eq("a@b.com"), anyString());
    }

    @Test
    void resetPassword_invalidToken_throws() {
        var ex = assertThrows(IllegalArgumentException.class,
                () -> service.reset(new ResetPasswordUseCase.Command(" ", "password123")));
        assertEquals("RESET_TOKEN_REQUIRED", ex.getMessage());
    }

    @Test
    void resetPassword_valid_updatesPassword_andMarksTokenUsed() {
        UUID userId = UUID.randomUUID();
        UUID tokenId = UUID.randomUUID();
        Instant now = Instant.now();

        String raw = "RESET_RAW";
        String hash = "sha256:" + raw;

        when(resetTokens.findValidByTokenHash(eq(hash), any(Instant.class)))
                .thenReturn(Optional.of(new PasswordResetTokenRepositoryPort.ResetTokenRecord(
                        tokenId, userId, hash, now.plusSeconds(100), null, now.minusSeconds(10)
                )));

        User user = new User(userId, "a@b.com", "john", "HASH:old", now.minusSeconds(1000), false);
        when(users.findById(userId)).thenReturn(Optional.of(user));

        service.reset(new ResetPasswordUseCase.Command(raw, "newpassword"));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(users).save(captor.capture());
        assertEquals("HASH:newpassword", captor.getValue().passwordHash());

        verify(resetTokens).markUsed(eq(tokenId), any(Instant.class));
    }

    @Test
    void adminCreate_valid_savesUser_andReturnsId() {
        when(users.findByEmail("a@b.com")).thenReturn(Optional.empty());
        when(users.findByUsername("john")).thenReturn(Optional.empty());

        UUID id = service.create(new AdminCreateUserUseCase.Command("a@b.com", "john", "password123", false));
        assertNotNull(id);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(users).save(captor.capture());
        User saved = captor.getValue();
        assertFalse(saved.isAdmin());
    }

    @Test
    void adminCreate_asAdmin_savesAdminUser() {
        when(users.findByEmail("a@b.com")).thenReturn(Optional.empty());
        when(users.findByUsername("john")).thenReturn(Optional.empty());

        UUID id = service.create(new AdminCreateUserUseCase.Command("a@b.com", "john", "password123", true));
        assertNotNull(id);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(users).save(captor.capture());
        User saved = captor.getValue();
        assertTrue(saved.isAdmin());
    }
}
