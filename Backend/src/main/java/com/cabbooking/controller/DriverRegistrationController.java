package com.cabbooking.controller;

import com.cabbooking.dto.DriverRegistrationRequest;
import com.cabbooking.model.Driver;
import com.cabbooking.service.DriverRegistrationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * DriverRegistrationController handles HTTP requests related to registering new drivers.
 * 
 * This REST controller exposes the endpoint "/api/drivers/register" where drivers can submit
 * their registration details.
 * 
 * Key Responsibilities:
 * - Receive driver registration requests as JSON.
 * - Validate incoming data using Jakarta Bean Validation.
 * - Delegate business logic of registration to DriverRegistrationService.
 * - Log registration attempts and results.
 * - Return appropriate HTTP responses based on the outcome.
 * 
 * Workflow:
 * 1. Drivers send POST request with registration details.
 * 2. Data validated, then passed to service for processing.
 * 3. Drivers are registered in the system but marked unverified by default.
 * 4. Admin must later verify the driver before activation.
 */
@RestController
@RequestMapping("/api/drivers")
@Validated  // Enables validation on method parameters
public class DriverRegistrationController {

    // Logger instance for logging registration events and errors
    private static final Logger logger = LoggerFactory.getLogger(DriverRegistrationController.class);

    // Service layer injected via Spring's Dependency Injection
    // Responsible for the actual registration logic (validation and persistence)
    @Autowired
    private DriverRegistrationService registrationService;

    /**
     * Endpoint to handle driver registration.
     * 
     * POST /api/drivers/register
     * 
     * Input:
     * - JSON payload mapped to DriverRegistrationRequest DTO.
     * - Validated automatically (@Valid annotation).
     * 
     * Process:
     * - Logs the registration attempt with the driver's username.
     * - Delegates to the DriverRegistrationService to register a new Driver.
     * - On success, logs success message with driver's ID.
     * - Returns HTTP 200 OK with message indicating driver is registered but unverified.
     * - Handles IllegalArgumentException (e.g., duplicate username, email, license)
     *   and returns HTTP 400 Bad Request with a helpful error message.
     * - Catches unexpected exceptions and returns HTTP 500 Internal Server Error.
     * 
     * @param request DriverRegistrationRequest DTO containing registration data from driver
     * @return ResponseEntity<String> HTTP response with status and informational message
     */
    @PostMapping("/register")
    public ResponseEntity<String> registerDriver(@Valid @RequestBody DriverRegistrationRequest request) {
        // Log registration attempt for audit and debugging
        logger.info("Driver registration attempt received for username: {}", request.getUsername());
        try {
            // Delegate registration logic to service
            Driver driver = registrationService.registerDriver(request);

            // Log registration success and return confirmation message
            logger.info("Driver registration successful (unverified), id: {}", driver.getId());
            return ResponseEntity.ok("Driver registered successfully, pending admin verification.");
        } catch (IllegalArgumentException e) {
            // Handle validation errors or duplicates appropriately
            logger.error("Driver registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Handle any other unexpected errors gracefully
            logger.error("Unexpected error during driver registration", e);
            return ResponseEntity.internalServerError().body("An error occurred during registration");
        }
    }
}
