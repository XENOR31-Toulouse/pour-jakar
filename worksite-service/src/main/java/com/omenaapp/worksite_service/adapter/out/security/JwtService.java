package com.omenaapp.worksite_service.adapter.out.security;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService {
  private final SecretKey key;

  public JwtService(@Value("${security.jwt.secret}") String secret) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  public Claims parseAndValidate(String jwt) {
    return Jwts.parser()
      .verifyWith(key)
      .build()
      .parseSignedClaims(jwt)
      .getPayload();
  }
}
