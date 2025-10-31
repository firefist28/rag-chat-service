package com.firefist.rag_chat_service.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "chat_message",
        indexes = {@Index(columnList = "session_id, created_at")})
public class ChatMessage {

    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(length = 36)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private ChatSession session;

    @Column(name = "sender", nullable = false, length = 32)
    private String sender; // USER / ASSISTANT / SYSTEM / RETRIEVED_CONTEXT

    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Lob
    @Column(name = "retrieved_context")
    private String retrievedContext;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "sequence_number")
    private Long sequenceNumber;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) this.createdAt = Instant.now();
        if (this.id == null) this.id = UUID.randomUUID();
    }

}

