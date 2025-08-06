package com.cabbooking.controller;

import com.cabbooking.dto.CustomerRegistrationRequest;
import com.cabbooking.model.Customer;
import com.cabbooking.service.CustomerRegistrationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * CustomerRegistrationController is a REST controller responsible for handling
 * HTTP requests related to customer registration in the Cab Booking Platform.
 * 
 * It provides an endpoint for clients to submit new customer sign-up details.
 * 
 * Main responsibilities:
 * - Receives HTTP POST requests at "/api/customers/register" with customer registration data.
 * - Validates input using Bean Validation (Jakarta Validation API).
 * - Calls the CustomerRegistrationService to perform registration business logic.
 * - Returns appropriate HTTP responses based on success or failure.
 * - Logs registration attempts and results for monitoring and debugging.
 * 
 * Usage:
 * - The client sends a JSON payload representing the customer's registration info.
 * - On success, returns HTTP 200 with a success message.
 * - On input errors or business validation failures, returns HTTP 400 with error details.
 * - On unexpected errors, returns HTTP 500 with a generic error message.
 */
@RestController
@RequestMapping("/api/customers")
@Validated
public class CustomerRegistrationController {

    // Logger instance for this class to track registration related events
    private static final Logger logger = LoggerFactory.getLogger(CustomerRegistrationController.class);

    // Service class injected by Spring to handle business logic for customer registration
    @Autowired
    private CustomerRegistrationService registrationService;

    /**
     * Endpoint to handle customer registration requests.
     * 
     * HTTP POST /api/customers/register
     * 
     * Request:
     * - Expects a JSON body matching CustomerRegistrationRequest DTO.
     * - Validated automatically thanks to @Valid annotation.
     * 
     * Workflow:
     * - Logs the incoming registration attempt with provided username for traceability.
     * - Delegates to the registrationService to create a new Customer entity.
     * - Logs success with the created customer's unique ID.
     * - Returns HTTP 200 OK with a simple success message on successful registration.
     * - If validation or uniqueness checks fail (e.g., username/email already taken),
     *   catches IllegalArgumentException, logs error, and returns HTTP 400 Bad Request.
     * - Catches any unexpected exceptions, logs the error with stack trace,
     *   and returns HTTP 500 Internal Server Error with a generic error message.
     * 
     * @param request CustomerRegistrationRequest DTO containing user-entered registration data
     * @return ResponseEntity<String> representing HTTP response and user message
     */
    @PostMapping("/register")
    public ResponseEntity<String> registerCustomer(@Valid @RequestBody CustomerRegistrationRequest request) {
        logger.info("Registration attempt for username: {}", request.getUsername());
        try {
            // Delegate customer registration logic to the service layer
            Customer customer = registrationService.registerCustomer(request);

            // Log and return success response
            logger.info("Customer registered successfully with id: {}", customer.getId());
            return ResponseEntity.ok("Customer registered successfully");

        } catch (IllegalArgumentException e) {
            // Handle validation failures or duplicate check errors (e.g., username/email exists)
            logger.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (Exception e) {
            // Handle unexpected server errors gracefully
            logger.error("Unexpected error during registration", e);
            return ResponseEntity.internalServerError().body("An error occurred");
        }
    }
}
