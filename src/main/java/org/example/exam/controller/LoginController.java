package org.example.exam.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

//Assignment 5
@RestController
public class LoginController {

    //Checks whether the logged-in user has the ADMIN or USER role and sends it back so the frontend knows which page to show.
    @GetMapping("/api/login")
    public ResponseEntity<Map<String, String>> login(HttpServletRequest request) {
        String role = request.isUserInRole("ADMIN") ? "ROLE_ADMIN" : "ROLE_USER";
        return ResponseEntity.ok(Map.of("role", role));
    }
}