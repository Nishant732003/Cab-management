package com.cabbooking.service;

import com.cabbooking.dto.LoginRequest;
import com.cabbooking.dto.LoginResponse;
import com.cabbooking.model.Admin;
import com.cabbooking.model.Customer;
import com.cabbooking.model.Driver;
import com.cabbooking.repository.AdminRepository;
import com.cabbooking.repository.CustomerRepository;
import com.cabbooking.repository.DriverRepository;
import com.cabbooking.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy; // <-- IMPORT THIS
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class LoginServiceImpl implements ILoginService, UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private DriverRepository driverRepository;
    
    // Inject the AuthenticationManager lazily to break the dependency cycle
    @Autowired
    @Lazy // <-- ADD THIS ANNOTATION
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        String identifier = loginRequest.getUsername() != null ? loginRequest.getUsername() : loginRequest.getEmail();
        logger.info("Attempting login for identifier: {}", identifier);

        try {
            // This will now work because the real AuthenticationManager is fetched only when this method is called
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(identifier, loginRequest.getPassword())
            );
        } catch (Exception e) {
            logger.error("Authentication failed for identifier: {}. Reason: {}", identifier, e.getMessage());
            throw new UsernameNotFoundException("Invalid credentials", e);
        }

        // --- THE REST OF YOUR METHOD IS UNCHANGED ---

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