package com.omenaapp.auth_service.adapter.out.security;


import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.omenaapp.auth_service.application.port.out.TokenIssuerPort;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenIssuer implements TokenIssuerPort {

  private final SecretKey key;
  private final long accessTtlSeconds;
  private final String issuer;

  public JwtTokenIssuer(
      @Value("${security.jwt.secret}") String secret,
      @Value("${security.jwt.access-ttl-seconds:900}") long accessTtlSeconds,
      @Value("${security.jwt.issuer:auth-service}") String issuer
  ) {
    // ⚠️ Le secret doit être suffisamment long (32+ chars mini)
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.accessTtlSeconds = accessTtlSeconds;
    this.issuer = issuer;
  }

  @Override
  public String issueAccessToken(UUID userId, Map<String, Object> claims) {
    Instant now = Instant.now();
    Instant exp = now.plusSeconds(accessTtlSeconds);

    return Jwts.builder()
        .issuer(issuer)
        .subject(userId.toString())
        .issuedAt(Date.from(now))
        .expiration(Date.from(exp))
        .claims(claims)
        .signWith(key)
        .compact();
  }
}
