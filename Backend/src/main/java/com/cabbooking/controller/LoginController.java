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
 * LoginController is a REST controller that manages user authentication requests.
 * 
 * Responsibilities:
 * - Handles HTTP POST requests for user login at "/api/login".
 * - Delegates authentication logic to the LoginService.
 * - Provides a simple GET endpoint "/api/login/test" for health checks to confirm controller availability.
 * 
 * Usage:
 * - Clients (such as frontend UI or Postman) send JSON login credentials to the POST endpoint.
 * - The controller receives the credentials wrapped in a LoginRequest DTO.
 * - It forwards the request to the LoginService which performs authentication.
 * - Upon successful authentication, a LoginResponse DTO with user details is returned.
 * - Logs events related to login attempts for audit and debugging.
 */
@RestController
@RequestMapping("/api/login")
public class LoginController {

    // SLF4J Logger instance for this controller
    // Useful for runtime monitoring and debugging
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    // LoginService instance is auto-injected by Spring's Dependency Injection.
    // This service encapsulates the core authentication/business logic.
    @Autowired
    private LoginService loginService;

    /**
     * Handles HTTP POST requests for user login.
     * 
     * Endpoint: POST /api/login
     * 
     * Request:
     * - Consumes JSON payload representing LoginRequest containing:
     *   - username: unique user login identifier
     *   - password: user plaintext password
     * 
     * Workflow:
     * - Logs the incoming login attempt with username.
     * - Calls the loginService.login() method which handles authentication.
     * - Service returns a LoginResponse indicating success or failure with user info.
     * - Logs the successful login event.
     * - Returns HTTP 200 OK with LoginResponse JSON body on success.
     * 
     * Note:
     * - Authentication failures should ideally throw exceptions handled globally with appropriate response codes (not shown here).
     * 
     * @param request LoginRequest DTO with username and password from client
     * @return ResponseEntity<LoginResponse> wrapping login result and user info
     */
    @PostMapping
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        // Log receipt of login attempt
        logger.info("Received login attempt for username: {}", request.getUsername());

        // Delegate authentication to LoginService
        LoginResponse response = loginService.login(request);

        // Log successful login
        logger.info("Login successful for username: {}", request.getUsername());

        // Return response to client with status 200
        return ResponseEntity.ok(response);
    }

    /**
     * Health-check endpoint for quick verification that login controller is active.
     * 
     * Endpoint: GET /api/login/test
     * 
     * Useful for:
     * - Automated services (monitoring, smoke tests).
     * - Developers manually verifying API readiness.
     * 
     * Workflow:
     * - Logs the test invocation at debug level.
     * - Returns simple plaintext message indicating controller is live.
     * 
     * @return String confirmation message
     */
    @GetMapping("/test")
    public String test() {
        // Debug log for test access tracking
        logger.debug("LoginController test endpoint called");

        // Return status message
        return "Login controller is active";
    }
}
