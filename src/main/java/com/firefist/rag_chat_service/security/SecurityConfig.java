package com.firefist.rag_chat_service.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ✅ Enable CORS using global WebConfig
                .cors(Customizer.withDefaults())

                // ✅ Disable CSRF for API endpoints
                .csrf(csrf -> csrf.disable())

                // ✅ Authorize requests
                .authorizeHttpRequests(auth -> auth
                        // Allow preflight requests (OPTIONS) through without auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Allow actuator and health checks
                        .requestMatchers("/actuator/**", "/health", "/api/v1/health").permitAll()

                        // Everything else still goes through your ApiKeyAuthFilter
                        .anyRequest().permitAll()
                );

        // No authentication manager needed since API key handled by custom filter
        return http.build();
    }
}
