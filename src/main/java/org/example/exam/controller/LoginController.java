package org.example.exam.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

//Tells the frontend who just logged in, so it knows whether to show the user or admin page.
@RestController
public class LoginController {

    @GetMapping("/api/login")
    public ResponseEntity<Map<String, String>> login(Authentication authentication) {
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("");
        return ResponseEntity.ok(Map.of("role", role));
    }
}