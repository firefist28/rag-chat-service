package com.firefist.rag_chat_service.service.retrieval;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RetrievalResult {
    private final String snippet;
    private final String source;
    private final double score;
}
