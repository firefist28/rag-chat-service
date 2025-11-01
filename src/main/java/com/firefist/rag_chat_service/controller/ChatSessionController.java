package com.firefist.rag_chat_service.controller;

import com.firefist.rag_chat_service.dto.CreateSessionRequest;
import com.firefist.rag_chat_service.dto.RenameSessionRequest;
import com.firefist.rag_chat_service.dto.SessionResponse;
import com.firefist.rag_chat_service.model.ChatSession;
import com.firefist.rag_chat_service.service.ChatSessionService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/sessions")
public class ChatSessionController {

    private final ChatSessionService service;

    public ChatSessionController(ChatSessionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SessionResponse> createSession(@Valid @RequestBody CreateSessionRequest req) {
        ChatSession created = service.createSession(req);

        SessionResponse resp = new SessionResponse(
                created.getId(),
                created.getTitle(),
                created.getUserId(),
                created.isFavorite(),
                created.getCreatedAt(),
                created.getUpdatedAt()
        );

        // Location header pointing to GET /api/v1/sessions/{id} (not implemented yet)
        URI location = URI.create("/api/v1/sessions/" + created.getId().toString());
        return ResponseEntity.created(location).body(resp);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ChatSession>> getSessionsByUserId(@PathVariable String userId) {
        List<ChatSession> sessions = service.getSessionsByUserId(userId);
        if (!sessions.isEmpty())
            return ResponseEntity.ok(sessions);
        else
            return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SessionResponse> getSession(@PathVariable("id") String id) {
        UUID uuid = UUID.fromString(id);
        log.info("Retrieving session by uid - {}", uuid.toString());
        ChatSession s = service.getById(uuid);
        if (s == null) return ResponseEntity.notFound().build();
        SessionResponse resp = new SessionResponse(
                s.getId(), s.getTitle(), s.getUserId(), s.isFavorite(), s.getCreatedAt(), s.getUpdatedAt()
        );
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/{id}/rename")
    public ResponseEntity<SessionResponse> renameSession(@PathVariable("id") String id,
                                                         @Valid @RequestBody RenameSessionRequest req) {
        UUID uuid = UUID.fromString(id);
        ChatSession updated = service.renameSession(uuid, req.getTitle());
        if (updated == null) return ResponseEntity.notFound().build();
        SessionResponse resp = new SessionResponse(
                updated.getId(), updated.getTitle(), updated.getUserId(), updated.isFavorite(),
                updated.getCreatedAt(), updated.getUpdatedAt()
        );
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/{id}/favorite")
    public ResponseEntity<SessionResponse> toggleFavorite(@PathVariable("id") String id) {
        UUID uuid = UUID.fromString(id);
        ChatSession updated = service.toggleFavorite(uuid);
        if (updated == null) return ResponseEntity.notFound().build();
        SessionResponse resp = new SessionResponse(
                updated.getId(), updated.getTitle(), updated.getUserId(), updated.isFavorite(),
                updated.getCreatedAt(), updated.getUpdatedAt()
        );
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable("id") String id) {
        UUID uuid = UUID.fromString(id);
        boolean deleted = service.softDeleteSession(uuid);
        if (!deleted) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }
}
