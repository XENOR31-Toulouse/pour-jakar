package com.omenaapp.auth_service.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.omenaapp.auth_service.application.service.AuthService;
import com.omenaapp.auth_service.domain.port.out.PasswordHasherPort;
import com.omenaapp.auth_service.domain.port.out.PasswordResetNotifierPort;
import com.omenaapp.auth_service.domain.port.out.PasswordResetTokenRepositoryPort;
import com.omenaapp.auth_service.domain.port.out.RefreshTokenRepositoryPort;
import com.omenaapp.auth_service.domain.port.out.TokenHasherPort;
import com.omenaapp.auth_service.domain.port.out.TokenIssuerPort;
import com.omenaapp.auth_service.domain.port.out.UserRepositoryPort;

@Configuration
public class ApplicationServicesConfig {

  @Bean
  AuthService authService(
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
    return new AuthService(
        users,
        hasher,
        tokenIssuer,
        refreshRepo,
        tokenHasher,
        resetTokens,
        resetNotifier,
        refreshTtlSeconds,
        resetTtlSeconds
    );
  }
}
