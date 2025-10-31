package com.firefist.rag_chat_service.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

/**
 * Configuration to create and register ApiKeyAuthFilter.
 * Reads API key(s) from environment variable API_KEY (single)
 * or SECURITY_APIKEY_KEYS (comma-separated).
 */
@Configuration
public class ApiKeyAuthConfig {

    // Master toggle to enable/disable API key checks (useful for tests/dev)
    @Value("${security.apikey.enabled:true}")
    private boolean enabled;

    // Single API key env var (preferred for quick setup)
    @Value("${API_KEY:}")
    private String envApiKey;

    // Fallback/alternate: comma-separated list of api keys from app config
    @Value("${security.apikey.keys:}")
    private String configuredKeys;

    // Whitelist patterns (ant-style)
    @Value("${security.apikey.whitelist:/api/v1/health,/swagger-ui/**,/v3/api-docs/**,/swagger-ui.html}")
    private String whitelistPatterns;

    @Bean
    public FilterRegistrationBean<ApiKeyAuthFilter> apiKeyAuthFilter() {
        FilterRegistrationBean<ApiKeyAuthFilter> registration = new FilterRegistrationBean<>();
        if (!enabled) {
            registration.setEnabled(false);
            return registration;
        }

        List<String> keys = resolveKeys();
        List<String> whitelist = resolveWhitelist();

        ApiKeyAuthFilter filter = new ApiKeyAuthFilter(keys, whitelist);
        registration.setFilter(filter);
        registration.addUrlPatterns("/*");
        registration.setOrder(10); // run fairly early
        return registration;
    }

    private List<String> resolveKeys() {
        Set<String> keys = new LinkedHashSet<>();
        if (envApiKey != null && !envApiKey.isBlank()) {
            keys.add(envApiKey.trim());
        }
        if (configuredKeys != null && !configuredKeys.isBlank()) {
            String[] parts = configuredKeys.split(",");
            for (String p : parts) {
                if (p != null && !p.isBlank()) {
                    keys.add(p.trim());
                }
            }
        }
        return new ArrayList<>(keys);
    }

    private List<String> resolveWhitelist() {
        List<String> list = new ArrayList<>();
        if (whitelistPatterns != null && !whitelistPatterns.isBlank()) {
            String[] parts = whitelistPatterns.split(",");
            for (String p : parts) {
                if (p != null && !p.isBlank()) list.add(p.trim());
            }
        }
        return list;
    }
}

