package com.firefist.rag_chat_service.service.retrieval;

import java.util.List;

public interface RetrievalService {
    /**
     * Retrieve top-k relevant snippets for the given query text.
     */
    List<RetrievalResult> retrieve(String query, int topK);
}
