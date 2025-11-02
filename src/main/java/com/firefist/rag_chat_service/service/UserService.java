package com.firefist.rag_chat_service.service;

import com.firefist.rag_chat_service.dto.UserRequest;
import com.firefist.rag_chat_service.model.User;
import com.firefist.rag_chat_service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Optional<User> createUser(UserRequest req) {
        String normalizedUserId = req.getUserId().trim();
        if (repository.findFirstByUserId(req.getUserId().trim()).isPresent()) {
            log.info("User already present, userId - {}", req.getUserId());
            return Optional.empty();
        }

        User u = new User();
        u.setUserId(normalizedUserId);
        // favorite false by default
        log.info("Creating user with userId - {}", normalizedUserId);
        return Optional.of(repository.save(u));
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserById(String userId) {
        if (userId == null) return Optional.empty();
        return repository.findFirstByUserId(userId.trim());
    }
}
