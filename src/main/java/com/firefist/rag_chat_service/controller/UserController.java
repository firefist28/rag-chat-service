package com.firefist.rag_chat_service.controller;

import com.firefist.rag_chat_service.dto.*;
import com.firefist.rag_chat_service.model.User;
import com.firefist.rag_chat_service.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
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
            UserResponse resp = new UserResponse(
                    created.get().getId(),
                    created.get().getUserId(),
                    created.get().getCreatedAt()
            );

            URI location = URI.create("/api/v1/user/" + created.get().getId().toString());
            return ResponseEntity.created(location).body(resp);
        }else {
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
    public ResponseEntity<Optional<User>> getUserByUserId(@PathVariable String userId) {
        Optional<User> user = service.getUserById(userId);
        if (user.isPresent())
            return ResponseEntity.ok(user);
        else
            return ResponseEntity.noContent().build();
    }
}
