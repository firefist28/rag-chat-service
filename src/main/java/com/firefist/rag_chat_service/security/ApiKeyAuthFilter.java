package com.firefist.rag_chat_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Filter that requires a valid API key in the X-API-KEY header for non-whitelisted endpoints.
 */
@Slf4j
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private static final String HEADER_NAME = "X-API-KEY";

    private final List<String> validApiKeys;
    private final List<String> whitelist;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public ApiKeyAuthFilter(List<String> validApiKeys, List<String> whitelist) {
        this.validApiKeys = validApiKeys;
        this.whitelist = whitelist;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        if (whitelist != null) {
            for (String pattern : whitelist) {
                if (pathMatcher.match(pattern, path)) {
                    log.info("Do not filter whiteList paths");
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            // Skip API key validation for preflight
            filterChain.doFilter(request, response);
            return;
        }

        String key = request.getHeader(HEADER_NAME);

        if (!isValidKey(key)) {
            // Unauthorized
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            String json = "{\"error\":\"Unauthorized\",\"message\":\"API key missing or invalid\"}";
            response.getWriter().write(json);
            log.info("Api Key is invalid. User is unauthorized with key - {}", key);
            return;
        }
        log.info("Api Key is valid. Processing further..");
        filterChain.doFilter(request, response);
    }

    private boolean isValidKey(String key) {
        if (key == null || key.isBlank()) return false;
        if (validApiKeys == null || validApiKeys.isEmpty()) return false;
        return validApiKeys.stream().filter(Objects::nonNull).anyMatch(k -> k.equals(key));
    }
}
