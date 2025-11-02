package com.firefist.rag_chat_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserResponse {
    private UUID id;
    private String userId;
    private Instant createdAt;
}
