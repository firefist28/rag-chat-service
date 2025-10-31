package com.firefist.rag_chat_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateMessageRequest {

    @NotBlank
    private String sender;

    @NotBlank
    private String content;

    // optional
    private String retrievedContext;

    // optional sequence number, used if client tracks ordering
    private Long sequenceNumber;
}

