package com.cabbooking.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cabbooking.dto.AdminRegistrationRequest;
import com.cabbooking.dto.CustomerRegistrationRequest;
import com.cabbooking.dto.DriverRegistrationRequest;
import com.cabbooking.dto.LoginRequest;
import com.cabbooking.dto.LoginResponse;
import com.cabbooking.service.IAdminRegistrationService;
import com.cabbooking.service.ICustomerRegistrationService;
import com.cabbooking.service.IDriverRegistrationService;
import com.cabbooking.service.ILoginService;
import com.cabbooking.service.LogoutService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * REST controller for handling authentication and registration for all user types.
 *
 * Main Responsibilities:
 * - Provides public endpoints for user login, logout, and registration.
 * - Consolidates all authentication-related actions into a single controller.
 * - Delegates business logic to the appropriate service layers.
 *
 * Security:
 * - These endpoints are publicly accessible to allow users to join and access the platform.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    // Logger for tracking authentication and registration events.
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private ILoginService loginService;

    @Autowired
    private LogoutService logoutService;

    @Autowired
    private IAdminRegistrationService adminRegistrationService;

    @Autowired
    private ICustomerRegistrationService customerRegistrationService;

    @Autowired
    private IDriverRegistrationService driverRegistrationService;

    /**
     * Handles POST requests to register a new admin.
     *
     * Endpoint: POST /api/auth/register/admin
     *
     * @param request AdminRegistrationRequest DTO containing registration data.
     * @return ResponseEntity<String> HTTP response with a success or error message.
     */
    @PostMapping("/register/admin")
    public ResponseEntity<String> registerAdmin(@Valid @RequestBody AdminRegistrationRequest request) {
        logger.info("Admin registration attempt for username: {}", request.getUsername());
        try {
            // Delegate to service layer for registration business logic
            adminRegistrationService.registerAdmin(request);
            logger.info("Admin registered successfully (unverified) for username: {}", request.getUsername());
            return ResponseEntity.ok("Admin registered successfully, pending superadmin verification.");
        } catch (IllegalArgumentException e) {
            // Handle known validation or duplicate data errors
            logger.error("Admin registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Handle unexpected errors
            logger.error("Unexpected error during admin registration", e);
            return ResponseEntity.internalServerError().body("An error occurred during registration");
        }
    }

    /**
     * Handles POST requests to register a new customer.
     *
     * Endpoint: POST /api/auth/register/customer
     *
     * @param request CustomerRegistrationRequest DTO containing registration data.
     * @return ResponseEntity<String> HTTP response with a success or error message.
     */
    @PostMapping("/register/customer")
    public ResponseEntity<String> registerCustomer(@Valid @RequestBody CustomerRegistrationRequest request) {
        logger.info("Customer registration attempt for username: {}", request.getUsername());
        try {
            // Delegate registration logic to the service layer
            customerRegistrationService.registerCustomer(request);
            logger.info("Customer registered successfully for username: {}", request.getUsername());
            return ResponseEntity.ok("Customer registered successfully");
        } catch (IllegalArgumentException e) {
            // Handle validation failures or duplicate data errors
            logger.error("Customer registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Handle unexpected server errors
            logger.error("Unexpected error during customer registration", e);
            return ResponseEntity.internalServerError().body("An error occurred");
        }
    }

    /**
     * Handles POST requests to register a new driver.
     *
     * Endpoint: POST /api/auth/register/driver
     *
     * @param request DriverRegistrationRequest DTO containing registration data.
     * @return ResponseEntity<String> HTTP response with a success or error message.
     */
    @PostMapping("/register/driver")
    public ResponseEntity<String> registerDriver(@Valid @RequestBody DriverRegistrationRequest request) {
        logger.info("Driver registration attempt for username: {}", request.getUsername());
        try {
            // Delegate registration logic to the service layer
            driverRegistrationService.registerDriver(request);
            logger.info("Driver registered successfully (unverified) for username: {}", request.getUsername());
            return ResponseEntity.ok("Driver registered successfully, pending admin verification.");
        } catch (IllegalArgumentException e) {
            // Handle validation or duplicate data errors
            logger.error("Driver registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Handle any other unexpected errors
            logger.error("Unexpected error during driver registration", e);
            return ResponseEntity.internalServerError().body("An error occurred during registration");
        }
    }

    /**
     * Handles HTTP POST requests for user login.
     *
     * Endpoint: POST /api/auth/login
     *
     * @param request LoginRequest DTO with username and password from the client.
     * @return ResponseEntity<LoginResponse> wrapping the login result, user info, and JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        logger.info("Received login attempt for username: {}", request.getUsername());
        // Delegate authentication to ILoginService
        LoginResponse response = loginService.login(request);
        logger.info("Login successful for username: {}", request.getUsername());
        // Return response to client with status 200
        return ResponseEntity.ok(response);
    }

    /**
     * Handles HTTP POST requests for user logout.
     *
     * Endpoint: POST /api/auth/logout
     *
     * @param request The incoming HttpServletRequest containing the authorization header.
     * @return ResponseEntity<String> with a success message.
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        // Check for Bearer token and blacklist it
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            logoutService.blacklistToken(token);
            logger.info("User successfully logged out and token blacklisted.");
        }
        return ResponseEntity.ok("Logged out successfully");
    }
}