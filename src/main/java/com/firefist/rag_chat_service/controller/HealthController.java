package com.firefist.rag_chat_service.controller;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
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

    @GetMapping("/testRL")
    @RateLimiter(name = "rl", fallbackMethod = "rlFallback")
    public ResponseEntity<?> testRateLimit(){
        return ResponseEntity.ok().body("Rate Limit Working");
    }

    // Option B: accept the specific Resilience4j exception
    public ResponseEntity<?> rlFallback(RequestNotPermitted ex) {
        return ResponseEntity.status(429).body("Rate Limit Reached..Please Wait..");
    }
}
