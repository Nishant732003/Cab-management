package com.cabbooking.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cabbooking.dto.AdminRegistrationRequest;
import com.cabbooking.dto.CustomerRegistrationRequest;
import com.cabbooking.dto.DriverRegistrationRequest;
import com.cabbooking.dto.EmailVerificationRequest;
import com.cabbooking.dto.LoginRequest;
import com.cabbooking.dto.LoginResponse;
import com.cabbooking.service.IAdminRegistrationService;
import com.cabbooking.service.ICustomerRegistrationService;
import com.cabbooking.service.IDriverRegistrationService;
import com.cabbooking.service.ILoginService;
import com.cabbooking.service.IUserDeletionService;
import com.cabbooking.service.ILogoutService;
import com.cabbooking.service.IVerificationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * REST controller for handling authentication related operations for all user
 * types. 
 * Main Responsibilities: 
 * - Provides public endpoints for registration, login, logout, email verification and account deletion. 
 * - Consolidates all authentication-related actions into a single controller. 
 * - Delegates business logic to the appropriate service layers. 
 * Security: 
 * - These endpoints are publicly accessible to allow users to join and access the platform.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // Logger for tracking authentication and registration events.
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    // Service layer injected to handle login operations.
    @Autowired
    private ILoginService loginService;

    // Service layer injected to handle logout operations.
    @Autowired
    private ILogoutService logoutService;

    // Service layer injected to handle admin registration operations.
    @Autowired
    private IAdminRegistrationService adminRegistrationService;

    // Service layer injected to handle customer registration operations.
    @Autowired
    private ICustomerRegistrationService customerRegistrationService;

    // Service layer injected to handle driver registration operations.
    @Autowired
    private IDriverRegistrationService driverRegistrationService;

    // Service layer injected to handle user deletion operations.
    @Autowired
    private IUserDeletionService userDeletionService;

    // Service layer injected to handle email verification operations.
    @Autowired
    private IVerificationService verificationService;

    /**
     * Endpoint handles requests to register a new admin. 
     * POST /api/auth/register/admin 
     * Workflow: 
     * - Person sends a AdminRegistrationRequest DTO with admin details. 
     * - Validates the request data. 
     * - Calls the service layer to handle registration logic. 
     * - Returns a success or error response.
     *
     * @param request AdminRegistrationRequest DTO containing registration data.
     * @return ResponseEntity<String> HTTP response with a success or error
     * message.
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
     * Endpoints handles requests to register a new customer. 
     * POST /api/auth/register/customer 
     * Workflow: 
     * - Person sends a CustomerRegistrationRequest DTO with customer details. 
     * - Validates the request data. 
     * - Calls the service layer to handle registration logic. 
     * - Returns a success or error response.
     *
     * @param request CustomerRegistrationRequest DTO containing registration
     * data.
     * @return ResponseEntity<String> HTTP response with a success or error
     * message.
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
     * Endpoint handles requests to register a new driver. 
     * POST /api/auth/register/driver 
     * Workflow: 
     * - Person sends a DriverRegistrationRequest DTO with driver details. 
     * - Validates the request data. 
     * - Calls the service layer to handle registration logic. 
     * - Returns a success or error response.
     *
     * @param request DriverRegistrationRequest DTO containing registration
     * data.
     * @return ResponseEntity<String> HTTP response with a success or error
     * message.
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
     * Endpoint handles requests for user login. 
     * POST /api/auth/login 
     * Workflow:
     * - Person sends a LoginRequest DTO with username and password. 
     * - Validates the request data. 
     * - Calls the service layer to authenticate the user. 
     * - Returns a LoginResponse with user info and JWT if successful. 
     * - Returns an error response if authentication fails.
     *
     * @param request LoginRequest DTO with username and password from the
     * client.
     * @return ResponseEntity<LoginResponse> wrapping the login result, user
     * info, and JWT.
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
     * Endpoint handles requests for user logout. 
     * POST /api/auth/logout
     * Workflow: 
     * - User sends a request with an Authorization header containing the Bearer token. 
     * - Extracts the token from the header. 
     * - Calls the service layer to blacklist the token. 
     * - Returns a success message if the token was blacklisted. 
     * - Returns an error response if the token is missing or invalid.
     *
     * @param request The incoming HttpServletRequest containing the
     * authorization header.
     * @return ResponseEntity<String> with a success message.
     */
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
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

    /**
     * Endpoint to delete any user account (Admin, Customer, or Driver). 
     * DELETE /api/auth/delete/{username} 
     * Workflow: 
     * - Admin or the user themselves can send a DELETE request with the username. 
     * - Validates the request to ensure the user exists. 
     * - Calls the service layer to handle user deletion. 
     * - Returns a success message if the user was deleted. 
     * - Returns an error response if the user does not exist or deletion fails.
     *
     * @param userId The ID of the user to delete.
     * @return A ResponseEntity with a success message.
     */
    @DeleteMapping("/delete/{username}")
    @PreAuthorize("principal == #username or hasRole('Admin') and isAuthenticated()")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        try {
            userDeletionService.deleteUser(username);
            logger.info("Successfully deleted user with username: {}", username);
            return ResponseEntity.ok("User with username " + username + " has been deleted.");
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to delete user with username {}: {}", username, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Endpoint to trigger sending a verification email to a user. 
     * POST /api/auth/send-verification-email 
     * Workflow: 
     * - User sends a request with their email address. 
     * - Validates the email format. 
     * - Calls the service layer to send a verification link. 
     * - Returns a success message if the email was sent. 
     * - Returns an error response if the email is invalid or sending fails.
     *
     * @param request DTO containing the user's email.
     * @return A success or error message.
     */
    @PostMapping("/send-verification-email")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> sendVerificationEmail(@Valid @RequestBody EmailVerificationRequest request) {
        try {
            verificationService.sendVerificationLink(request.getEmail());
            return ResponseEntity.ok("A verification link has been sent to your email address.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Endpoint that the user is directed to from the verification email. It
     * validates the token and updates the user's verification status. 
     * GET /api/auth/verify-email 
     * Workflow: 
     * - User clicks the verification link in their email. 
     * - The link contains a token as a query parameter. 
     * - Validates the token by calling the service layer. 
     * - If valid, updates the user's status to verified. 
     * - Returns a success message if verification is successful. 
     * - Returns an error message if the token is invalid or expired.
     *
     * @param token The verification token from the URL query parameter.
     * @return A ResponseEntity with a success or failure message.
     */
    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        // Delegate the verification logic to the service
        boolean isVerified = verificationService.verifyToken(token);

        if (isVerified) {
            // On success, return a 200 OK
            return ResponseEntity.ok("Your email has been successfully verified!");
        } else {
            // On failure, return a 400 Bad Request
            return ResponseEntity.badRequest().body("The verification link is invalid or has expired.");
        }
    }
}
