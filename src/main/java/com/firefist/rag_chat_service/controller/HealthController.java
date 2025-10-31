package com.firefist.rag_chat_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Minimal health endpoint for dev and k8s readiness.
 */
@RestController
public class HealthController {

    @GetMapping("/api/v1/health")
    public ResponseEntity<?> health() {
//        return ResponseEntity.ok().body("{\"status\":\"UP\"}");
        return ResponseEntity.ok().body(Map.of("status","UP"));
    }
}
