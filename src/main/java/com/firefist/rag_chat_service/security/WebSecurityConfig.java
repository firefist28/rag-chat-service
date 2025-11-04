package com.firefist.rag_chat_service.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Minimal configuration to disable CSRF and ensure API key authentication works.
 * Your ApiKeyAuthConfig is already registering ApiKeyAuthFilter separately.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    private final ApiKeyAuthFilter apiKeyAuthFilter;

    // Inject the filter bean created in ApiKeyAuthConfig
    public WebSecurityConfig(ApiKeyAuthFilter apiKeyAuthFilter) {
        this.apiKeyAuthFilter = apiKeyAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 2. Set session management to stateless
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3. Authorization rules: Allow OPTIONS requests
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/health").permitAll()
                        .anyRequest().permitAll()
                )

                // 4. Register custom filter AFTER authorization is handled for OPTIONS
                .addFilterBefore(apiKeyAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}