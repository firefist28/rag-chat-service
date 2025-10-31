package com.firefist.rag_chat_service.service.retrieval;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple stub that returns canned snippets. Replace with real retrieval later.
 */
@Service
public class MockRetrievalService implements RetrievalService {

    @Override
    public List<RetrievalResult> retrieve(String query, int topK) {
        List<RetrievalResult> out = new ArrayList<>();
        // return up to topK simple canned snippets that reference the query for testing
        for (int i = 1; i <= topK; i++) {
            String snippet = String.format("Snippet %d for query: \"%s\" — (sample context)", i, query);
            out.add(new RetrievalResult(snippet, "mock-source-" + i, 1.0 - (i * 0.1)));
        }
        return out;
    }
}
