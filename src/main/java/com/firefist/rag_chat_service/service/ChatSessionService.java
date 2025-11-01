package com.firefist.rag_chat_service.service;

import com.firefist.rag_chat_service.dto.CreateSessionRequest;
import com.firefist.rag_chat_service.model.ChatSession;
import com.firefist.rag_chat_service.repository.ChatSessionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class ChatSessionService {

    private final ChatSessionRepository repository;

    public ChatSessionService(ChatSessionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ChatSession createSession(CreateSessionRequest req) {
        ChatSession s = new ChatSession();
        s.setTitle(req.getTitle());
        s.setUserId(req.getUserId());
        // favorite false by default
        log.info("Creating session with userId - {}", req.getUserId());
        return repository.save(s);
    }

    @Transactional (readOnly = true)
    public List<ChatSession> getSessionsByUserId(String userId) {
        return repository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public ChatSession getById(UUID id) {
        Optional<ChatSession> opt = repository.findById(id);
        if (opt.isEmpty()) return null;
        ChatSession s = opt.get();
        log.info("Session retrieved - {}, ", s);
        if (s.getDeletedAt() != null) return null;
        return s;
    }

    @Transactional
    public ChatSession renameSession(UUID id, String newTitle) {
        ChatSession s = repository.findById(id).orElse(null);
        if (s == null || s.getDeletedAt() != null) return null;
        s.setTitle(newTitle);
        log.info("Renamed session to {}", newTitle);
        // updatedAt managed by @PreUpdate
        return repository.save(s);
    }

    @Transactional
    public ChatSession toggleFavorite(UUID id) {
        ChatSession s = repository.findById(id).orElse(null);
        if (s == null || s.getDeletedAt() != null) return null;
        log.info("Favourite Session set to id - {}", s.getId());
        s.setFavorite(!s.isFavorite());
        return repository.save(s);
    }

    @Transactional
    public boolean softDeleteSession(UUID id) {
        ChatSession s = repository.findById(id).orElse(null);
        if (s == null || s.getDeletedAt() != null) return false;
        s.setDeletedAt(Instant.now());
        repository.save(s);
        log.info("Soft Deleted session: id - {}", s.getId());
        return true;
    }
}
