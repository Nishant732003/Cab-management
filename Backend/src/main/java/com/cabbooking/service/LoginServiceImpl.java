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
import com.cabbooking.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the ILoginService interface.
 * 
 * This service is responsible for authenticating users of various types:
 * - Admin
 * - Driver
 * - Customer
 * 
 * The authentication process checks the users in the following order:
 * - Admin repository
 * - Driver repository
 * - Customer repository
 * 
 * It uses Spring Security's PasswordEncoder to verify the hashed password
 * stored in the database against the plaintext password provided by the user.
 * On successful authentication, it generates a JWT token and returns it as
 * part of the LoginResponse.
 * 
 * Important:
 * - Throws AuthenticationException on invalid login credentials or if no matching user is found.
 * - Logs login attempts, successes, and failures for monitoring and debugging.
 */
@Service
public class LoginServiceImpl implements ILoginService {

    private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

    /*
     * Repository for Customer entity.
     * Provides CRUD operations for Customer entities.
     */
    @Autowired
    private CustomerRepository customerRepository;

    /*
     * Repository for Admin entity.
     * Provides CRUD operations for Admin entities.
     */
    @Autowired
    private AdminRepository adminRepository;

    /*
     * Repository for Driver entity.
     * Provides CRUD operations for Driver entities.
     */
    @Autowired
    private DriverRepository driverRepository;

    /*
     * Password encoder for securely hashing and comparing passwords.
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /*
     * JWT utility class for generating and validating JWT tokens.
     */
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Attempts to authenticate a user based on username and password.
     * Checks each user type repository in order: Admin -> Driver -> Customer.
     * 
     * Workflow:
     * - Searches for an Admin with the given username; if found, verifies password:
     *      * If password matches, generates JWT token and returns LoginResponse with admin info and token.
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
     * @return LoginResponse with success message, user ID, user type, and JWT token on successful authentication
     * @throws AuthenticationException if username/password invalid or user not found
     */
    @Override
    public LoginResponse login(LoginRequest request) {
        logger.info("Login attempt received for username: {}", request.getUsername());

        // Admin authentication
        Admin admin = adminRepository.findByUsername(request.getUsername());
        if (admin != null) {
            if (passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
                logger.info("Admin authentication successful for username: {}", request.getUsername());

                String token = jwtUtil.generateToken(admin.getUsername(), "Admin");
                return new LoginResponse("Admin login successful", admin.getId(), "Admin", token);
            } else {
                logger.warn("Admin authentication failed - invalid password for username: {}", request.getUsername());
                throw new AuthenticationException("Invalid username or password");
            }
        }

        // Driver authentication
        Driver driver = driverRepository.findByUsername(request.getUsername());
        if (driver != null) {
            if (passwordEncoder.matches(request.getPassword(), driver.getPassword())) {
                logger.info("Driver authentication successful for username: {}", request.getUsername());

                String token = jwtUtil.generateToken(driver.getUsername(), "Driver");
                return new LoginResponse("Driver login successful", driver.getId(), "Driver", token);
            } else {
                logger.warn("Driver authentication failed - invalid password for username: {}", request.getUsername());
                throw new AuthenticationException("Invalid username or password");
            }
        }

        // Customer authentication
        Customer customer = customerRepository.findByUsername(request.getUsername());
        if (customer != null) {
            if (passwordEncoder.matches(request.getPassword(), customer.getPassword())) {
                logger.info("Customer authentication successful for username: {}", request.getUsername());

                String token = jwtUtil.generateToken(customer.getUsername(), "Customer");
                return new LoginResponse("Customer login successful", customer.getId(), "Customer", token);
            } else {
                logger.warn("Customer authentication failed - invalid password for username: {}", request.getUsername());
                throw new AuthenticationException("Invalid username or password");
            }
        }

        logger.warn("Authentication failed - user not found for username: {}", request.getUsername());
        throw new AuthenticationException("Invalid username or password");
    }
}
