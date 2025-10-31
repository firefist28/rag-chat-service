package com.firefist.rag_chat_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageResponse {
    private UUID id;
    private UUID sessionId;
    private String sender;
    private String content;
    private String retrievedContext;
    private Instant createdAt;
    private Long sequenceNumber;
}
