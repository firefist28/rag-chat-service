package com.firefist.rag_chat_service.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

/**
 * Configuration to create and register ApiKeyAuthFilter.
 * Supports multiple API keys via:
 *  - env API_KEYS (comma separated)  <-- highest precedence
 *  - env API_KEY (single)            <-- backward compatibility
 *  - property security.apikey.keys  (comma separated)
 */
@Configuration
public class ApiKeyAuthConfig {

    // Master toggle to enable/disable API key checks (useful for tests/dev)
    @Value("${security.apikey.enabled:true}")
    private boolean enabled;

    // Highest precedence: comma-separated list from env
    @Value("${API_KEYS:}")
    private String envApiKeys;

    // Backwards compatible single key env var
    @Value("${API_KEY:}")
    private String envApiKey;

    // Configured keys in application.properties (comma-separated)
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
        // Use LinkedHashSet to preserve order and dedupe
        Set<String> keys = new LinkedHashSet<>();

        // 1) API_KEYS env var (comma separated) - highest precedence
        if (envApiKeys != null && !envApiKeys.isBlank()) {
            String[] parts = envApiKeys.split(",");
            for (String p : parts) {
                if (p != null && !p.isBlank()) keys.add(p.trim());
            }
            // if API_KEYS was set, return these keys (but still allow fallback to configuredKeys to be appended)
        }

        // 2) single API_KEY env var (backwards compat)
        if ((envApiKeys == null || envApiKeys.isBlank())
                && envApiKey != null && !envApiKey.isBlank()) {
            keys.add(envApiKey.trim());
        }

        // 3) configured keys in properties (append if not present)
        if (configuredKeys != null && !configuredKeys.isBlank()) {
            String[] parts = configuredKeys.split(",");
            for (String p : parts) {
                if (p != null && !p.isBlank()) keys.add(p.trim());
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
