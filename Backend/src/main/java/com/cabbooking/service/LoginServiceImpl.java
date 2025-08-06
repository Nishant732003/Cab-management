package com.cabbooking.service;

import com.cabbooking.dto.LoginRequest;
import com.cabbooking.dto.LoginResponse;
import com.cabbooking.exception.AuthenticationException;
import com.cabbooking.model.Customer;
import com.cabbooking.model.Admin;
import com.cabbooking.model.Driver;
import com.cabbooking.repository.CustomerRepository;
import com.cabbooking.repository.AdminRepository;
import com.cabbooking.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the LoginService interface.
 * 
 * This service is responsible for authenticating users of various types:
 * - Admin
 * - Driver
 * - Customer
 * 
 * The authentication process checks the users in the following order:
 * 1. Admin repository
 * 2. Driver repository
 * 3. Customer repository
 * 
 * It uses Spring Security's PasswordEncoder to verify the hashed password
 * stored in the database against the plaintext password provided by the user.
 * 
 * Important:
 * - Throws AuthenticationException on invalid login credentials or if no matching user is found.
 * - Logs login attempts, successes, and failures for monitoring and debugging.
 */
@Service
public class LoginServiceImpl implements LoginService {

    // Logger for recording login attempt details and errors
    private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

    // Spring Data JPA repositories for accessing persistent user data by username
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private DriverRepository driverRepository;

    // PasswordEncoder to compare raw password from request with hashed password from DB
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Attempts to authenticate a user based on username and password.
     * Checks each user type repository in order: Admin -> Driver -> Customer.
     * 
     * Workflow:
     * - Searches for an Admin with the given username; if found, verifies password:
     *      * If password matches, returns a successful LoginResponse with admin info.
     *      * Otherwise, throws AuthenticationException for invalid credentials.
     * - If not found/failed, repeats above steps for Driver repository.
     * - If still no match, repeats for Customer repository.
     * - If no user found in any repository, throws AuthenticationException.
     * 
     * Logging:
     * - Logs login attempt receipt.
     * - Logs successful authentications including username.
     * - Logs authentication failures including invalid password attempts and missing users.
     * 
     * @param request LoginRequest object containing username and plaintext password
     * @return LoginResponse with success message, user ID, and user type on successful auth
     * @throws AuthenticationException if username/password invalid or user not found
     */
    @Override
    public LoginResponse login(LoginRequest request) {
        logger.info("Login attempt received for username: {}", request.getUsername());

        // 1. Attempt Admin authentication
        Admin admin = adminRepository.findByUsername(request.getUsername());
        if (admin != null) {
            if (passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
                logger.info("Admin authentication successful for username: {}", request.getUsername());
                return new LoginResponse("Admin login successful", admin.getId(), "Admin");
            } else {
                logger.warn("Admin authentication failed - invalid password for username: {}", request.getUsername());
                throw new AuthenticationException("Invalid username or password");
            }
        }

        // 2. Attempt Driver authentication
        Driver driver = driverRepository.findByUsername(request.getUsername());
        if (driver != null) {
            if (passwordEncoder.matches(request.getPassword(), driver.getPassword())) {
                logger.info("Driver authentication successful for username: {}", request.getUsername());
                return new LoginResponse("Driver login successful", driver.getId(), "Driver");
            } else {
                logger.warn("Driver authentication failed - invalid password for username: {}", request.getUsername());
                throw new AuthenticationException("Invalid username or password");
            }
        }

        // 3. Attempt Customer authentication
        Customer customer = customerRepository.findByUsername(request.getUsername());
        if (customer != null) {
            if (passwordEncoder.matches(request.getPassword(), customer.getPassword())) {
                logger.info("Customer authentication successful for username: {}", request.getUsername());
                return new LoginResponse("Customer login successful", customer.getId(), "Customer");
            } else {
                logger.warn("Customer authentication failed - invalid password for username: {}", request.getUsername());
                throw new AuthenticationException("Invalid username or password");
            }
        }

        // No user found in any repository
        logger.warn("Authentication failed - user not found for username: {}", request.getUsername());
        throw new AuthenticationException("Invalid username or password");
    }
}
