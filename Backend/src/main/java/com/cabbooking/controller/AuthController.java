package com.cabbooking.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.cabbooking.model.Admin; 

import com.cabbooking.dto.AdminRegistrationRequest;
import com.cabbooking.dto.CustomerRegistrationRequest;
import com.cabbooking.dto.DriverRegistrationRequest;
import com.cabbooking.dto.EmailVerificationRequest;
import com.cabbooking.dto.LoginRequest;
import com.cabbooking.dto.LoginResponse;
import com.cabbooking.repository.AdminRepository;
import com.cabbooking.repository.CustomerRepository;
import com.cabbooking.repository.DriverRepository;
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
 * REST controller for handling authentication and registration for all user
 * types.
 *
 * Main Responsibilities: - Provides public endpoints for user login, logout,
 * and registration. - Consolidates all authentication-related actions into a
 * single controller. - Delegates business logic to the appropriate service
 * layers.
 *
 * Security: - These endpoints are publicly accessible to allow users to join
 * and access the platform.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // Logger for tracking authentication and registration events.
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private ILoginService loginService;

    @Autowired
    private ILogoutService logoutService;

    @Autowired
    private IAdminRegistrationService adminRegistrationService;

    @Autowired
    private ICustomerRegistrationService customerRegistrationService;

    @Autowired
    private IDriverRegistrationService driverRegistrationService;

    @Autowired
    private IUserDeletionService userDeletionService;

    @Autowired
    private IVerificationService verificationService;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DriverRepository driverRepository;

    /**
     * Handles POST requests to register a new admin.
     *
     * Endpoint: POST /api/auth/register/admin
     *
     * @param request AdminRegistrationRequest DTO containing registration data.
     * @return ResponseEntity<String> HTTP response with a success or error
     * message.
     */
    @PostMapping("/register/admin")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody AdminRegistrationRequest request) {
        logger.info("Admin registration attempt for username: {}", request.getUsername());
        try {
            // Delegate to service layer, which now returns the created Admin object
            Admin newAdmin = adminRegistrationService.registerAdmin(request);
            logger.info("Admin registered successfully (unverified) for username: {}", request.getUsername());
            
            // --- MODIFIED: Return the complete 'newAdmin' object with a 200 OK status ---
            return ResponseEntity.ok(newAdmin);
            
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
     * Handles POST requests to register a new driver.
     *
     * Endpoint: POST /api/auth/register/driver
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
     * Handles HTTP POST requests for user login.
     *
     * Endpoint: POST /api/auth/login
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
     * Handles HTTP POST requests for user logout.
     *
     * Endpoint: POST /api/auth/logout
     *
     * @param request The incoming HttpServletRequest containing the
     * authorization header.
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

    /**
     * Endpoint to delete any user account (Admin, Customer, or Driver).
     *
     * @param userId The ID of the user to delete.
     * @return A ResponseEntity with a success message.
     */
    @DeleteMapping("/delete/{username}")
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
     *
     * @param request DTO containing the user's email.
     * @return A success or error message.
     */
    @PostMapping("/send-verification-email")
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
    
    /**
     * Checks if a username already exists across all user types.
     * * Endpoint: GET /api/auth/check/username/{username}
     *
     * @param username The username to check.
     * @return ResponseEntity<Boolean> true if the username exists, false otherwise.
     */
    @GetMapping("/check/username/{username}")
    public ResponseEntity<Boolean> checkUsername(@PathVariable String username) {
        boolean exists = adminRepository.existsByUsername(username) || 
                         customerRepository.existsByUsername(username) || 
                         driverRepository.existsByUsername(username);
        return ResponseEntity.ok(exists);
    }
}
