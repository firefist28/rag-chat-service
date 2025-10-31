package com.firefist.rag_chat_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateSessionRequest {

    @NotBlank(message = "title is required")
    private String title;
    private String userId;
}

