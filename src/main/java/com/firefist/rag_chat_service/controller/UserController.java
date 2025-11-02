package com.firefist.rag_chat_service.controller;

import com.firefist.rag_chat_service.dto.*;
import com.firefist.rag_chat_service.model.User;
import com.firefist.rag_chat_service.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest req) {
        Optional<User> created = service.createUser(req);

        if (created.isPresent()) {
            User user = created.get();
            UserResponse resp = new UserResponse(
                    user.getId(),
                    user.getUserId(),
                    user.getCreatedAt()
            );

            log.info("User created successfully with id: {}", user.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } else {
            log.warn("User already exists with userId: {}", req.getUserId());
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new UserResponse(
                            null,
                            req.getUserId(),
                            null
                    ));
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserByUserId(@PathVariable String userId) {
        Optional<User> user = service.getUserById(userId);

        if (user.isPresent()) {
            log.info("User found with userId: {}", userId);
            return ResponseEntity.ok(user.get());
        } else {
            log.warn("User not found with userId: {}", userId);
            return ResponseEntity.notFound().build();
        }
    }
}