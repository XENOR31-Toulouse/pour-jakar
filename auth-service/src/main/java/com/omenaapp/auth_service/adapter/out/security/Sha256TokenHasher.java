package com.omenaapp.auth_service.adapter.out.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.springframework.stereotype.Component;

import com.omenaapp.auth_service.domain.port.out.TokenHasherPort;


@Component
public class Sha256TokenHasher implements TokenHasherPort {
  @Override
  public String sha256(String value) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] digest = md.digest(value.getBytes(StandardCharsets.UTF_8));
      StringBuilder sb = new StringBuilder();
      for (byte b : digest) sb.append(String.format("%02x", b));
      return sb.toString();
    } catch (Exception e) {
      throw new RuntimeException("HASH_ERROR", e);
    }
  }
}