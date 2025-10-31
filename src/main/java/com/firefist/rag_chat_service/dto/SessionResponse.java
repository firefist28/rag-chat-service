package com.firefist.rag_chat_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SessionResponse {
    private UUID id;
    private String title;
    private String userId;
    private boolean favorite;
    private Instant createdAt;
    private Instant updatedAt;
}
