package com.cabbooking.service;

import com.cabbooking.dto.AdminRegistrationRequest;
import com.cabbooking.model.Admin;
import com.cabbooking.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    // AdminRepository handles CRUD and query operations for Admin entities
    @Autowired
    private AdminRepository adminRepository;

    // PasswordEncoder hashes plaintext passwords for secure storage
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Registers a new admin user.
     * 
     * Workflow:
     * 1. Check if the username is already taken by querying the repository.
     *    - If taken, throw IllegalArgumentException with error message.
     * 2. Check if the provided email is already registered by fetching all admins
     *    and searching by email ignoring case.
     *    - If email exists, throw IllegalArgumentException.
     * 3. Create a new Admin entity and populate it with data from the request DTO.
     * 4. Hash the password before setting on the Admin entity.
     * 5. Set the "verified" flag to false to indicate that the admin is not activated yet.
     * 6. Save the new Admin entity to the database and return it.
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
        // Check for existing admin by username
        Optional<Admin> existingByUsername = Optional.ofNullable(adminRepository.findByUsername(request.getUsername()));
        if (existingByUsername.isPresent()) {
            throw new IllegalArgumentException("Username is already taken.");
        }

        // Check for existing email (inefficient in-memory search; consider a DB-level method)
        boolean emailExists = adminRepository.findAll().stream()
            .anyMatch(a -> a.getEmail() != null && a.getEmail().equalsIgnoreCase(request.getEmail()));
        if (emailExists) {
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
