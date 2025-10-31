package com.firefist.rag_chat_service.repository;

import com.firefist.rag_chat_service.model.ChatMessage;
import com.firefist.rag_chat_service.model.ChatSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {
    Page<ChatMessage> findBySessionAndSessionDeletedAtIsNullOrderByCreatedAtAsc(ChatSession session, Pageable pageable);
    // fallback simpler:
    Page<ChatMessage> findBySessionOrderByCreatedAtAsc(ChatSession session, Pageable pageable);
}