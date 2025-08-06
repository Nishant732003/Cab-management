package com.cabbooking.controller;

import com.cabbooking.dto.AdminRegistrationRequest;
import com.cabbooking.model.Admin;
import com.cabbooking.service.AdminRegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * AdminRegistrationController is a REST controller for handling HTTP requests
 * related to the registration of new Admin users in the system.
 * 
 * This controller exposes endpoints under "/api/admins" path.
 * 
 * Main Responsibilities:
 * - Receive admin registration requests with admin details in JSON.
 * - Validate the input using Jakarta Bean Validation (@Valid).
 * - Delegate registration business logic to AdminRegistrationService.
 * - Handle and log registration success and error scenarios.
 * - Respond with appropriate HTTP status codes and messages.
 *
 * Security:
 * - This endpoint is publicly accessible to allow new admin registrations.
 * - Newly registered admins are marked as unverified and require superadmin approval.
 *
 * Workflow:
 * - Client sends POST request to /api/admins/register with admin registration data.
 * - Controller validates the incoming DTO.
 * - Calls service layer to attempt admin creation.
 * - Returns 200 OK with success message if registration succeeds.
 * - Returns 400 Bad Request if input validation or uniqueness constraints fail.
 * - Returns 500 Internal Server Error for unexpected failures.
 */
@RestController
@RequestMapping("/api/admins")
@Validated
public class AdminRegistrationController {

    // Logger for logging registration attempts and errors
    private static final Logger logger = LoggerFactory.getLogger(AdminRegistrationController.class);

    // Injected service responsible for business logic related to admin registration
    @Autowired
    private AdminRegistrationService registrationService;

    /**
     * Handles POST requests to register a new admin.
     * 
     * Endpoint: POST /api/admins/register
     * 
     * Request:
     * - JSON body mapped to AdminRegistrationRequest DTO.
     * - DTO is validated (@Valid) for required fields and constraints.
     * 
     * Process:
     * - Logs the incoming admin username for tracking.
     * - Calls the registrationService.registerAdmin() method to perform registration.
     * - If registration succeeds, logs successful admin id.
     * - Returns HTTP 200 OK with a success message indicating registration status.
     * - Catches IllegalArgumentException for validation or uniqueness violations,
     *   logs error, and returns HTTP 400 Bad Request with error message.
     * - Catches any other unexpected exceptions, logs error details,
     *   and returns HTTP 500 Internal Server Error with generic message.
     * 
     * @param request AdminRegistrationRequest DTO containing registration data
     * @return ResponseEntity<String> HTTP response with success or error message
     */
    @PostMapping("/register")
    public ResponseEntity<String> registerAdmin(@Valid @RequestBody AdminRegistrationRequest request) {
        logger.info("Admin registration attempt for username: {}", request.getUsername());
        try {
            // Delegate to service layer for registration business logic
            Admin admin = registrationService.registerAdmin(request);

            // Successful registration; admin created but unverified by superadmin
            logger.info("Admin registered successfully (unverified), id: {}", admin.getId());

            // Return HTTP 200 OK with info message
            return ResponseEntity.ok("Admin registered successfully, pending superadmin verification.");
        } catch (IllegalArgumentException e) {
            // Handle known validation or duplicate username/email errors
            logger.error("Admin registration failed: {}", e.getMessage());

            // Return HTTP 400 Bad Request with error detail
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Handle unexpected errors gracefully
            logger.error("Unexpected error during admin registration", e);

            // Return HTTP 500 Internal Server Error with generic message
            return ResponseEntity.internalServerError().body("An error occurred during registration");
        }
    }
}
