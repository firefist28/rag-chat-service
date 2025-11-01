package com.firefist.rag_chat_service.controller;

import com.firefist.rag_chat_service.dto.CreateMessageRequest;
import com.firefist.rag_chat_service.dto.MessageResponse;
import com.firefist.rag_chat_service.model.ChatMessage;
import com.firefist.rag_chat_service.service.ChatMessageService;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/sessions/{sessionId}/messages")
public class ChatMessageController {

    private final ChatMessageService messageService;

    public ChatMessageController(ChatMessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public ResponseEntity<?> getMessages(
            @PathVariable("sessionId") String sessionId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "50") int size,
            @RequestParam(name = "sort", defaultValue = "asc") String sort
    ) {
            UUID sid = UUID.fromString(sessionId);
            Sort.Direction dir = "desc".equalsIgnoreCase(sort) ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(dir, "createdAt"));
            Page<ChatMessage> p = messageService.getMessages(sid, pageable);
            if (p.isEmpty()) return ResponseEntity.noContent().build();
            var body = p.stream().map(m -> new MessageResponse(
                    m.getId(),
                    m.getSession().getId(),
                    m.getSender(),
                    m.getContent(),
                    m.getRetrievedContext(),
                    m.getCreatedAt(),
                    m.getSequenceNumber()
            )).collect(Collectors.toList());
            return ResponseEntity.ok().body(body);
    }

    @PostMapping
    public ResponseEntity<?> addMessage(@PathVariable("sessionId") String sessionId,
                                        @Valid @RequestBody CreateMessageRequest req) {
            UUID sid = UUID.fromString(sessionId);
            ChatMessage saved = messageService.addMessage(sid, req);
            if (saved == null) return ResponseEntity.notFound().build();
            MessageResponse resp = new MessageResponse(
                    saved.getId(),
                    saved.getSession().getId(),
                    saved.getSender(),
                    saved.getContent(),
                    saved.getRetrievedContext(),
                    saved.getCreatedAt(),
                    saved.getSequenceNumber()
            );
            return ResponseEntity.status(201).body(resp);
    }
}
