package com.firefist.rag_chat_service.service.llm;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.StringJoiner;

/**
 * Simple LLM stub that composes a reply using retrieved snippets and returns it.
 */
@Service
@ConditionalOnProperty(name = "llm.enabled", havingValue = "false", matchIfMissing = true)
public class MockLlmClientService implements LlmClient {

    @Override
    @RateLimiter(name = "rl", fallbackMethod = "llmRateLimitFallback")
    public LlmResponse generate(String userMessage, List<String> retrievedSnippets) {
        // existing mock logic — compose a reply from retrievedSnippets
        StringJoiner joiner = new StringJoiner("\n\n");
        for (String s : retrievedSnippets) {
            joiner.add(s);
        }

        String reply = "Assistant (mock): processed message: " + userMessage
                + "\n\ncontext:\n" + joiner.toString();;

        return new LlmResponse(reply, "mock-model-1.0");
    }

    /**
     * Fallback invoked when rate limiter denies the call (RequestNotPermitted)
     * Method signature must match original params + a Throwable (or RequestNotPermitted)
     */
    public LlmResponse llmRateLimitFallback(String userMessage, List<String> retrievedSnippets, RequestNotPermitted ex) {
        // return a friendly message. You can also persist a message, or return null depending on your service flow.
        String msg = "LLM temporarily rate-limited. Please retry after a short while.";
        return new LlmResponse(msg,"mock-model-1.0");
    }
}

