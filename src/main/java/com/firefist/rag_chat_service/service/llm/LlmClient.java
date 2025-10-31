package com.firefist.rag_chat_service.service.llm;

import java.util.List;

public interface LlmClient {
    /**
     * Generate assistant response given user message and retrieved context snippets.
     */
    LlmResponse generate(String userMessage, List<String> retrievedSnippets);
}
