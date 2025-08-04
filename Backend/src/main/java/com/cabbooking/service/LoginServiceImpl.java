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
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of LoginService interface.
 * 
 * Responsible for authenticating users of different types:
 * Admin, Driver, and Customer.
 * 
 * Login checks order:
 * 1. Admin Repository
 * 2. Driver Repository
 * 3. Customer Repository
 * 
 * Logs key events for monitoring and debugging purposes.
 * Throws AuthenticationException if credentials are invalid or user not found.
 */
@Service
public class LoginServiceImpl implements LoginService {

    private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private DriverRepository driverRepository;

    /**
     * Tries to authenticate the user based on username and password.
     * Checks repositories in the order: Admin -> Driver -> Customer.
     * 
     * @param request The login request containing username and password.
     * @return LoginResponse containing login status and user info.
     * @throws AuthenticationException on invalid credentials or user absence.
     */
    @Override
    public LoginResponse login(LoginRequest request) {
        logger.info("Login attempt received for username: {}", request.getUsername());

        // 1. Try to find and authenticate Admin
        Admin admin = adminRepository.findByUsername(request.getUsername());
        if (admin != null) {
            if (admin.getPassword().equals(request.getPassword())) {
                logger.info("Admin authentication successful for username: {}", request.getUsername());
                return new LoginResponse("Admin login successful", admin.getId(), "Admin");
            } else {
                logger.warn("Admin authentication failed - invalid password for username: {}", request.getUsername());
                throw new AuthenticationException("Invalid username or password");
            }
        }

        // 2. Try to find and authenticate Driver
        Driver driver = driverRepository.findByUsername(request.getUsername());
        if (driver != null) {
            if (driver.getPassword().equals(request.getPassword())) {
                logger.info("Driver authentication successful for username: {}", request.getUsername());
                return new LoginResponse("Driver login successful", driver.getId(), "Driver");
            } else {
                logger.warn("Driver authentication failed - invalid password for username: {}", request.getUsername());
                throw new AuthenticationException("Invalid username or password");
            }
        }

        // 3. Try to find and authenticate Customer
        Customer customer = customerRepository.findByUsername(request.getUsername());
        if (customer != null) {
            if (customer.getPassword().equals(request.getPassword())) {
                logger.info("Customer authentication successful for username: {}", request.getUsername());
                return new LoginResponse("Customer login successful", customer.getId(), "Customer");
            } else {
                logger.warn("Customer authentication failed - invalid password for username: {}", request.getUsername());
                throw new AuthenticationException("Invalid username or password");
            }
        }

        // User not found in any repository
        logger.warn("Authentication failed - user not found for username: {}", request.getUsername());
        throw new AuthenticationException("Invalid username or password");
    }
}
