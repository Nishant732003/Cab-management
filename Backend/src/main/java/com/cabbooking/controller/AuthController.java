package com.cabbooking.controller;

import com.cabbooking.dto.AdminRegistrationRequest;
import com.cabbooking.dto.CustomerRegistrationRequest;
import com.cabbooking.dto.DriverRegistrationRequest;
import com.cabbooking.dto.LoginRequest;
import com.cabbooking.dto.LoginResponse;
import com.cabbooking.model.Admin;
import com.cabbooking.model.Customer;
import com.cabbooking.model.Driver;
import com.cabbooking.service.AdminRegistrationService;
import com.cabbooking.service.CustomerRegistrationService;
import com.cabbooking.service.DriverRegistrationService;
import com.cabbooking.service.LoginService;
import com.cabbooking.service.LogoutService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api")
public class AuthController {
    // SLF4J Logger instance for this controller
    // Useful for runtime monitoring and debugging
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    // LoginService instance is auto-injected by Spring's Dependency Injection.
    // This service encapsulates the core authentication/business logic.
    @Autowired
    private LoginService loginService;
    
    @Autowired
    private LogoutService logoutService;

    // Injected service responsible for business logic related to admin registration
    @Autowired
    private AdminRegistrationService adminRegistrationService;

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
    @PostMapping("/admin/register")
    public ResponseEntity<String> registerAdmin(@Valid @RequestBody AdminRegistrationRequest request) {
        logger.info("Admin registration attempt for username: {}", request.getUsername());
        try {
            // Delegate to service layer for registration business logic
            Admin admin = adminRegistrationService.registerAdmin(request);

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

    // Service class injected by Spring to handle business logic for customer registration
    @Autowired
    private CustomerRegistrationService customerRegistrationService;

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
    @PostMapping("/customer/register")
    public ResponseEntity<String> registerCustomer(@Valid @RequestBody CustomerRegistrationRequest request) {
        logger.info("Registration attempt for username: {}", request.getUsername());
        try {
            // Delegate customer registration logic to the service layer
            Customer customer = customerRegistrationService.registerCustomer(request);

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

    @Autowired
    private DriverRegistrationService driverRegistrationService;

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
    @PostMapping("/driver/register")
    public ResponseEntity<String> registerDriver(@Valid @RequestBody DriverRegistrationRequest request) {
        // Log registration attempt for audit and debugging
        logger.info("Driver registration attempt received for username: {}", request.getUsername());
        try {
            // Delegate registration logic to service
            Driver driver = driverRegistrationService.registerDriver(request);

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
    @PostMapping("/login")
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
