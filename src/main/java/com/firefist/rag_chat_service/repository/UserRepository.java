package com.firefist.rag_chat_service.repository;

import com.firefist.rag_chat_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findFirstByUserId(String userId);   // or keep findByUserId if needed
    boolean existsByUserId(String userId);
}