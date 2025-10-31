package com.firefist.rag_chat_service.service.llm;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LlmResponse {
    private final String generatedText;
    private final String model;
}
