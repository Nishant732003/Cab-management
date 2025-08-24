package com.cabbooking.service;

// import java.util.Optional;

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
 * 
 * This service handles the business logic for registering new Admin users
 * into the system. It performs necessary validations such as checking for
 * unique username and email before creating an Admin entity with hashed password.
 * 
 * Newly registered admins are created with a verified status set to false,
 * indicating they require approval by a superadmin before activation.
 * 
 * Dependencies:
 * - AdminRepository for database persistence and uniqueness checks.
 * - PasswordEncoder for securely hashing admin passwords.
 */
@Service
public class AdminRegistrationServiceImpl implements IAdminRegistrationService {

    /**
     * Repository to handle CRUD operations for Admin entities.
     * Provides methods to check for existing usernames and emails.
     */
    @Autowired
    private AdminRepository adminRepository;

    /*
     * Repository to handle CRUD operations for Customer entities.
     * Provides methods to check for existing usernames and emails.
     */
    @Autowired
    private CustomerRepository customerRepository;

    /*
     * Repository to handle CRUD operations for Driver entities.
     * Provides methods to check for existing usernames and emails.
     */
    @Autowired
    private DriverRepository driverRepository;

    /*
     * PasswordEncoder for securely hashing admin passwords.
     * Used for password storage and comparison.
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Registers a new admin user.
     * 
     * Workflow:
     * - Check if the username is already taken by querying the repository.
     *    - If taken, throw IllegalArgumentException with error message.
     * - Check if the provided email is already registered by fetching all admins
     *    and searching by email ignoring case.
     *    - If email exists, throw IllegalArgumentException.
     * - Create a new Admin entity and populate it with data from the request DTO.
     * - Hash the password before setting on the Admin entity.
     * - Set the "verified" flag to false to indicate that the admin is not activated yet.
     * - Save the new Admin entity to the database and return it.
     * 
     * Important Notes:
     * - Email uniqueness check currently fetches all admins and filters in-memory,
     *   which might be inefficient with large data sets. Consider adding a repository
     *   method for email existence check for better performance.
     * - Password hashing uses BCryptPasswordEncoder for security best practices.
     * 
     * @param request AdminRegistrationRequest DTO containing registration input data
     * @return created Admin entity persisted in the database
     * @throws IllegalArgumentException if username or email is already in use
     */
    @Override
    public Admin registerAdmin(AdminRegistrationRequest request) {

        if (adminRepository.existsByUsername(request.getUsername()) || customerRepository.existsByUsername(request.getUsername()) || driverRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already taken.");
        }

        // Check for existing email at the database level
        if (adminRepository.existsByEmail(request.getEmail()) || customerRepository.existsByEmail(request.getEmail()) || driverRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        // Create new Admin entity and populate fields
        Admin admin = new Admin();
        admin.setUsername(request.getUsername());

        // Hash the raw password before storage to ensure security
        admin.setPassword(passwordEncoder.encode(request.getPassword()));

        admin.setEmail(request.getEmail());
        admin.setAddress(request.getAddress());
        admin.setMobileNumber(request.getMobileNumber());

        // Newly created admins must be verified by superadmin before activation
        admin.setVerified(false);

        // Save the entity to the database and return the persisted instance
        return adminRepository.save(admin);
    }
}
