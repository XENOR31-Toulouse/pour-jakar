package com.omenaapp.auth_service.adapter.in.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
      .csrf(csrf -> csrf.disable()) // DEV only (sinon il faut gérer CSRF token)
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/", "/index.html", "/**/*.js", "/**/*.css").permitAll()
        .requestMatchers("/auth/**").permitAll()
        .anyRequest().permitAll()
      )
      .httpBasic(Customizer.withDefaults())
      .build();
  }
}
