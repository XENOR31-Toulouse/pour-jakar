package com.omenaapp.auth_service.domain.port.out;
import java.util.Map;
import java.util.UUID;

public interface TokenIssuerPort {
  String issueAccessToken(UUID userId, Map<String, Object> claims);
}
