package com.firefist.rag_chat_service.service;

import com.firefist.rag_chat_service.dto.CreateMessageRequest;
import com.firefist.rag_chat_service.model.ChatMessage;
import com.firefist.rag_chat_service.model.ChatSession;
import com.firefist.rag_chat_service.repository.ChatMessageRepository;
import com.firefist.rag_chat_service.repository.ChatSessionRepository;
import com.firefist.rag_chat_service.service.llm.LlmClient;
import com.firefist.rag_chat_service.service.llm.LlmResponse;
import com.firefist.rag_chat_service.service.retrieval.RetrievalResult;
import com.firefist.rag_chat_service.service.retrieval.RetrievalService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ChatMessageService {

    private final ChatMessageRepository messageRepository;
    private final ChatSessionRepository sessionRepository;
    private final RetrievalService retrievalService;
    private final LlmClient llmClient;

    // how many snippets to fetch for now
    private static final int DEFAULT_TOP_K = 3;

    public ChatMessageService(ChatMessageRepository messageRepository, ChatSessionRepository sessionRepository,
                              RetrievalService retrievalService,
                              LlmClient llmClient) {
        this.messageRepository = messageRepository;
        this.sessionRepository = sessionRepository;
        this.retrievalService = retrievalService;
        this.llmClient = llmClient;
    }

    @Transactional(readOnly = true)
    public Page<ChatMessage> getMessages(UUID sessionId, Pageable pageable) {
        ChatSession session = sessionRepository.findById(sessionId).orElse(null);
        if (session == null || session.getDeletedAt() != null) return Page.empty(pageable);
        // use repository method
        return messageRepository.findBySessionOrderByCreatedAtAsc(session, pageable);
    }

    /**
     * Adds a message. If sender == "USER", triggers retrieval + LLM pipeline synchronously,
     * persists assistant message and returns the saved message for the created assistant reply.
     *
     * If sender is other than USER, simply saves the message and returns the saved message.
     *
     * Returns:
     * - for USER: the assistant ChatMessage entity that was generated and saved.
     * - for non-USER: the saved ChatMessage (echo).
     */
    @Transactional
    public ChatMessage addMessage(UUID sessionId, CreateMessageRequest req) {
        ChatSession session = sessionRepository.findById(sessionId).orElse(null);
        if (session == null || session.getDeletedAt() != null) return null;

        // 1) Save incoming message (user or otherwise)
        ChatMessage incoming = new ChatMessage();
        incoming.setSession(session);
        incoming.setSender(req.getSender());
        incoming.setContent(req.getContent());
        incoming.setRetrievedContext(req.getRetrievedContext());
        incoming.setSequenceNumber(req.getSequenceNumber());
        messageRepository.save(incoming);

        // If message is from USER, run pipeline and return assistant reply
        if ("USER".equalsIgnoreCase(req.getSender())) {
            // 2) Retrieval
            List<RetrievalResult> results = retrievalService.retrieve(req.getContent(), DEFAULT_TOP_K);
            List<String> snippets = results.stream()
                    .map(RetrievalResult::getSnippet)
                    .collect(Collectors.toList());

            // 3) Call LLM to generate assistant reply
            LlmResponse llmResp = llmClient.generate(req.getContent(), snippets);

            // 4) Persist assistant message with retrieved context stored (JSON/text)
            ChatMessage assistant = new ChatMessage();
            assistant.setSession(session);
            assistant.setSender("ASSISTANT");
            assistant.setContent(llmResp.getGeneratedText());

            // Save retrieved snippets concatenated (simple approach)
            String joinedContext = snippets.stream().collect(Collectors.joining("\n\n---\n\n"));
            assistant.setRetrievedContext(joinedContext);

            assistant.setSequenceNumber(incoming.getSequenceNumber() == null ? null : incoming.getSequenceNumber() + 1);

            // 5) Return assistant message so controller can return it to client
            return messageRepository.save(assistant);
        }

        // non-user messages: return the saved incoming message
        return incoming;
    }
}