package com.firefist.rag_chat_service.repository;

import com.firefist.rag_chat_service.model.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChatSessionRepository extends JpaRepository<ChatSession, UUID> {
    // extra query methods will go here (findByUserId, findByFavorite, etc.)
}