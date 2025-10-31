package com.firefist.rag_chat_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RenameSessionRequest {
    @NotBlank(message = "title is required")
    private String title;
}
