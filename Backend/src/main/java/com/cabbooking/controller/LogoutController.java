package com.cabbooking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cabbooking.service.LogoutService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class LogoutController {

    @Autowired
    private LogoutService logoutService;

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            logoutService.blacklistToken(token);
        }
        return ResponseEntity.ok("Logged out successfully");
    }
}