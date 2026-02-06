package com.omenaapp.auth_service.adapter.in.web;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.omenaapp.auth_service.adapter.out.security.JwtService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class JwtAuthFilter extends OncePerRequestFilter {

  private final JwtService jwtService;

  public JwtAuthFilter(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {

    String header = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (header == null || !header.startsWith("Bearer ")) {
      chain.doFilter(request, response);
      return;
    }

    String token = header.substring("Bearer ".length()).trim();

    try {
      Claims claims = jwtService.parseAndValidate(token);

      String userId = claims.getSubject(); // UUID string
      String role = (String) claims.get("role"); // "ADMIN" / "USER"

      var authorities = (role == null)
          ? List.<SimpleGrantedAuthority>of()
          : List.of(new SimpleGrantedAuthority("ROLE_" + role));

      var auth = new UsernamePasswordAuthenticationToken(userId, null, authorities);
      SecurityContextHolder.getContext().setAuthentication(auth);

    } catch (Exception e) {
      // Token invalide/expiré => on nettoie le contexte, et on continue.
      SecurityContextHolder.clearContext();
    }

    chain.doFilter(request, response);
  }
}
