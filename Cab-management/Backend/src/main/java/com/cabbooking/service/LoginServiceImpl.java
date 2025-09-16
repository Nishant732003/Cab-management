package com.cabbooking.service;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cabbooking.dto.LoginRequest;
import com.cabbooking.dto.LoginResponse;
import com.cabbooking.model.Admin;
import com.cabbooking.model.Customer;
import com.cabbooking.model.Driver;
import com.cabbooking.repository.AdminRepository;
import com.cabbooking.repository.CustomerRepository;
import com.cabbooking.repository.DriverRepository;
import com.cabbooking.security.JwtUtil;

/*
 * Service class for handling user login functionality.
 * Implements ILoginService and UserDetailsService for Spring Security integration.
 * 
 * Main Responsibilities:
 * - Authenticate users based on username/email and password.
 * - Generate JWT tokens upon successful authentication.
 * - Load user details for Spring Security.
 * 
 * Dependencies:
 * - AdminRepository: Access admin user data.
 * - CustomerRepository: Access customer user data.
 * - DriverRepository: Access driver user data.
 * - AuthenticationManager: Perform authentication checks.
 * - JwtUtil: Generate JWT tokens.
 */
@Service
public class LoginServiceImpl implements ILoginService, UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

    /*
     * Provides access to the admin repository.
     */
    @Autowired
    private AdminRepository adminRepository;

    /*
     * Provides access to the customer repository.
     */
    @Autowired
    private CustomerRepository customerRepository;

    /*
     * Provides access to the driver repository.
     */
    @Autowired
    private DriverRepository driverRepository;

    /*
     * Provides access to the authentication manager for verifying credentials.
     */
    @Autowired
    @Lazy
    private AuthenticationManager authenticationManager;
    
    /*
     * Provides access to the JWT utility for token generation.
     */
    @Autowired
    private JwtUtil jwtUtil;

    /*
     * Login a user (Admin, Driver, or Customer) based on username/email and password.
     * 
     * Workflow:
     * - Authenticate the user using AuthenticationManager.
     * - Identify the user type (Admin, Driver, Customer) by searching in respective repositories.
     * - Generate a JWT token for the authenticated user.
     * - Return a LoginResponse containing user details and the JWT token.
     * - Throws UsernameNotFoundException if authentication fails or user is not found.
     * 
     * @param loginRequest The login request containing username/email and password.
     * @return LoginResponse containing user details and JWT token.
     * @throws UsernameNotFoundException if authentication fails or user is not found.
     */
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        
        // Determine whether to use username or email for authentication
        String identifier = loginRequest.getUsername() != null ? loginRequest.getUsername() : loginRequest.getEmail();
        
        logger.info("Attempting login for identifier: {}", identifier);

        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(identifier, loginRequest.getPassword())
            );
        } catch (AuthenticationException e) {
            logger.error("Authentication failed for identifier: {}. Reason: {}", identifier, e.getMessage());
            throw new UsernameNotFoundException("Invalid credentials", e);
        }

        Admin admin = adminRepository.findByUsername(identifier);
        if (admin == null) {
            admin = adminRepository.findByEmail(identifier);
        }
        if (admin != null) {
            logger.info("Admin user '{}' logged in successfully.", identifier);
            final String token = jwtUtil.generateToken(admin.getUsername(), "Admin");
            return new LoginResponse("Admin login successful", admin.getId(), "Admin", token, true);
        }

        Driver driver = driverRepository.findByUsername(identifier);
        if (driver == null) {
            driver = driverRepository.findByEmail(identifier);
        }
        if (driver != null) {
            logger.info("Driver user '{}' logged in successfully.", identifier);
            final String token = jwtUtil.generateToken(driver.getUsername(), "Driver");
            return new LoginResponse("Driver login successful", driver.getId(), "Driver", token, true);
        }

        Customer customer = customerRepository.findByUsername(identifier);
        if (customer == null) {
            customer = customerRepository.findByEmail(identifier);
        }
        if (customer != null) {
            logger.info("Customer user '{}' logged in successfully.", identifier);
            final String token = jwtUtil.generateToken(customer.getUsername(), "Customer");
            return new LoginResponse("Customer login successful", customer.getId(), "Customer", token, true);
        }

        logger.error("User not found after successful authentication for identifier: {}", identifier);
        throw new UsernameNotFoundException("User details not found in any repository post-authentication.");
    }

    /*
     * Load user details by username or email for Spring Security.
     * 
     * Workflow:
     * - Searches for the user in Admin, Driver, and Customer repositories using the provided identifier.
     * - If found, returns a UserDetails object containing username, password, and authorities.
     * - If not found, throws UsernameNotFoundException.
     * 
     * @param identifier The username or email of the user to load.
     * @return UserDetails object for the user.
     * @throws UsernameNotFoundException if the user is not found in any repository.
     */
    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        logger.debug("Loading user by identifier for Spring Security: {}", identifier);

        Admin admin = adminRepository.findByUsername(identifier);
        if (admin == null) {
            admin = adminRepository.findByEmail(identifier);
        }
        if (admin != null) {
            return new org.springframework.security.core.userdetails.User(admin.getUsername(), admin.getPassword(), new ArrayList<>());
        }

        Driver driver = driverRepository.findByUsername(identifier);
        if (driver == null) {
            driver = driverRepository.findByEmail(identifier);
        }
        if (driver != null) {
            return new org.springframework.security.core.userdetails.User(driver.getUsername(), driver.getPassword(), new ArrayList<>());
        }

        Customer customer = customerRepository.findByUsername(identifier);
        if (customer == null) {
            customer = customerRepository.findByEmail(identifier);
        }
        if (customer != null) {
            return new org.springframework.security.core.userdetails.User(customer.getUsername(), customer.getPassword(), new ArrayList<>());
        }

        logger.warn("User not found for identifier: {}", identifier);
        throw new UsernameNotFoundException("User not found with identifier: " + identifier);
    }
}