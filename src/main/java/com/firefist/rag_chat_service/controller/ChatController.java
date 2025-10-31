package com.firefist.rag_chat_service.controller;

import com.firefist.rag_chat_service.service.llm.LlmResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    @PostMapping("/generate")
    @Operation(summary = "Generate assistant reply",
            description = "Send user message + retrieved snippets to LLM", security = @SecurityRequirement(name = "ApiKeyAuth"))
    @ApiResponse(responseCode = "200", description = "Assistant response returned")
    public ResponseEntity<LlmResponse> generate(
            @Parameter(description = "User input message", required = true)
            @RequestParam String message) {
        return ResponseEntity.ok().body(new LlmResponse("Pending...", "model"));
    }
}
