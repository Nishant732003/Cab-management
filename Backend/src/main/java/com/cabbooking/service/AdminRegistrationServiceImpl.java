package com.cabbooking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cabbooking.dto.AdminRegistrationRequest;
import com.cabbooking.model.Admin;
import com.cabbooking.repository.AdminRepository;
import com.cabbooking.repository.CustomerRepository;
import com.cabbooking.repository.DriverRepository;

/**
 * Implementation of the IAdminRegistrationService interface.
 * Handles the business logic for registering new Admin users.
 */
@Service
public class AdminRegistrationServiceImpl implements IAdminRegistrationService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Registers a new admin user.
     *
     * @param request AdminRegistrationRequest DTO containing registration input data
     * @return created Admin entity persisted in the database
     * @throws IllegalArgumentException if username or email is already in use
     */
    @Override
    public Admin registerAdmin(AdminRegistrationRequest request) {
        // Check if the username is already taken across all user types.
        if (adminRepository.existsByUsername(request.getUsername()) || 
            customerRepository.existsByUsername(request.getUsername()) || 
            driverRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already taken.");
        }

        // Check if the email is already registered across all user types.
        if (adminRepository.existsByEmail(request.getEmail()) || 
            customerRepository.existsByEmail(request.getEmail()) || 
            driverRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        // Create new Admin entity and populate its fields.
        Admin admin = new Admin();
        admin.setUsername(request.getUsername());
        
        // --- MODIFIED: Set the name from the request DTO ---
        admin.setName(request.getName());

        // Hash the raw password before storing it to ensure security.
        admin.setPassword(passwordEncoder.encode(request.getPassword()));

        admin.setEmail(request.getEmail());
        admin.setAddress(request.getAddress());
        admin.setMobileNumber(request.getMobileNumber());

        // Newly created admins must be verified by a superadmin before activation.
        admin.setVerified(false);

        // Save the entity to the database and return the persisted instance.
        return adminRepository.save(admin);
    }
}