package com.cabbooking.controller;

import com.cabbooking.dto.LoginRequest;
import com.cabbooking.dto.LoginResponse;
import com.cabbooking.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * chekinig git test
 * LoginController handles HTTP requests related to user login.
 * 
 * This REST controller provides API endpoints under "/api/login" for:
 * - User login via POST /api/login with credentials in the request body.
 * - A simple GET /api/login/test endpoint to check if controller is active.
 * 
 * Incoming login requests are delegated to the LoginService for authentication.
 * Success and failure outcomes are logged using SLF4J.
 * 
 * This controller is a critical entry point for user authentication in the Cab Booking Platform.
 */
@RestController
@RequestMapping("/api/login")
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private LoginService loginService;

    /**
     * Processes login requests.
     * 
     * POST method expects JSON body with username and password.
     * Delegates authentication logic to LoginService.
     * Returns JSON response indicating login success/failure.
     *
     * @param request the login request containing username and password
     * @return ResponseEntity wrapping LoginResponse with login status
     */
    @PostMapping
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        logger.info("Received login attempt for username: {}", request.getUsername());
        LoginResponse response = loginService.login(request);
        logger.info("Login successful for username: {}", request.getUsername());
        return ResponseEntity.ok(response);
    }

    /**
     * Simple health check endpoint to ensure controller is active.
     * Access via GET /api/login/test.
     *
     * @return simple string message indicating controller status
     */
    @GetMapping("/test")
    public String test() {
        logger.debug("LoginController test endpoint called");
        return "Login controller is active";
    }
}
