package com.firefist.rag_chat_service.service.llm;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.*;

/**
 * Simple RestTemplate-based OpenAI-style LLM client.
 *
 * - Sends a system message, user message and concatenated retrieved snippets.
 * - Expects a JSON response containing a generated text under the path
 *   (this implementation assumes vendor returns a top-level "choices"[0]."message". "content"
 *   or "choices"[0]."text" — the parser attempts both).
 *
 * Configure with properties (see application.properties snippet).
 */
@Service
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "llm.enabled", havingValue = "true")
public class OpenAiLlmClient implements LlmClient {

    private final RestTemplate rest;
    private final String apiKey;
    private final String endpoint;
    private final String model;
    private final int timeoutMs;

    public OpenAiLlmClient(RestTemplateBuilder builder,
                           @Value("${llm.api.key}") String apiKey,
                           @Value("${llm.endpoint}") String endpoint,
                           @Value("${llm.model:gpt-4o-mini}") String model,
                           @Value("${llm.timeout-ms:15000}") int timeoutMs) {
        this.apiKey = apiKey;
        this.endpoint = endpoint;
        this.model = model;
        this.timeoutMs = timeoutMs;
        this.rest = builder
                .setConnectTimeout(Duration.ofMillis(timeoutMs))
                .setReadTimeout(Duration.ofMillis(timeoutMs))
                .build();
    }

    /**
     * Primary generate method called by ChatMessageService.
     * Wrapped by a rate limiter with fallback (see method below).
     */
    @Override
    @RateLimiter(name = "rl", fallbackMethod = "llmRateLimitFallback")
    public LlmResponse generate(String userMessage, List<String> retrievedSnippets) {
        Objects.requireNonNull(userMessage, "userMessage required");

        // Build messages list: system -> (optional) context -> user
        List<Map<String, String>> messages = new ArrayList<>();

        String systemPrompt = "You are an assistant that answers clearly and concisely. " +
                "Be helpful and reference any provided context when useful.";
        messages.add(Map.of("role", "system", "content", systemPrompt));

        if (retrievedSnippets != null && !retrievedSnippets.isEmpty()) {
            String joined = String.join("\n\n---\n\n", retrievedSnippets);
            // include as a system/context message so it's available as grounding
            messages.add(Map.of("role", "system", "content", "Retrieved context:\n" + joined));
        }

        messages.add(Map.of("role", "user", "content", userMessage));

        Map<String, Object> payload = new HashMap<>();
        payload.put("model", model);
        payload.put("messages", messages);
        // optional: limit tokens, temperature etc.
        payload.put("max_tokens", 800);
        payload.put("temperature", 0.2);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (apiKey != null && !apiKey.isBlank()) {
            headers.setBearerAuth(apiKey);
        }

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<Map> resp = rest.postForEntity(endpoint, request, Map.class);
            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                String generated = parseGeneratedText(resp.getBody());
                return new LlmResponse(generated, model);
            } else {
                String err = "LLM provider returned non-2xx: " + resp.getStatusCode();
                return new LlmResponse(err, model);
            }
        } catch (RestClientException ex) {
            String err = "LLM request failed: " + ex.getMessage();
            return new LlmResponse(err, model);
        }
    }

    /**
     * Best-effort parser: many vendors return either:
     * - {"choices":[{"message": {"content": "..."}}], ...}
     * - {"choices":[{"text":"..."}], ...}
     */
    @SuppressWarnings("unchecked")
    private String parseGeneratedText(Map body) {
        try {
            Object choicesObj = body.get("choices");
            if (choicesObj instanceof List) {
                List choices = (List) choicesObj;
                if (!choices.isEmpty()) {
                    Object first = choices.get(0);
                    if (first instanceof Map) {
                        Map firstMap = (Map) first;
                        Object message = firstMap.get("message");
                        if (message instanceof Map) {
                            Object content = ((Map) message).get("content");
                            if (content != null) return content.toString();
                        }
                        Object text = firstMap.get("text");
                        if (text != null) return text.toString();
                    } else if (first instanceof String) {
                        return first.toString();
                    }
                }
            }
        } catch (Exception ignored) {}
        // fallback: try top-level "text" or "content"
        Object text = body.get("text");
        if (text != null) return text.toString();
        Object content = body.get("content");
        if (content != null) return content.toString();
        return "LLM: could not parse response";
    }

    /**
     * Rate limiter fallback — signature matches (params..., Throwable)
     * Returns a friendly message to be persisted by ChatMessageService.
     */
    public LlmResponse llmRateLimitFallback(String userMessage, List<String> retrievedSnippets, Throwable ex) {
        String msg = "LLM temporarily unavailable (rate limit or error). Please try again later.";
        return new LlmResponse(msg, model);
    }
}
