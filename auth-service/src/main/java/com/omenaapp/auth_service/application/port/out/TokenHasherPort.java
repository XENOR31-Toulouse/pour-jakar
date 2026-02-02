package com.omenaapp.auth_service.application.port.out;

public interface TokenHasherPort {
  String sha256(String value);
}