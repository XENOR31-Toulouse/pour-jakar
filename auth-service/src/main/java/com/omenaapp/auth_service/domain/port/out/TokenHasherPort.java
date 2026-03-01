package com.omenaapp.auth_service.domain.port.out;

public interface TokenHasherPort {
  String sha256(String value);
}