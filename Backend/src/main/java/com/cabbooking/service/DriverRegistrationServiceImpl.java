package com.cabbooking.service;

// import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cabbooking.dto.DriverRegistrationRequest;
import com.cabbooking.model.Driver;
import com.cabbooking.repository.DriverRepository;
import com.cabbooking.repository.AdminRepository;
import com.cabbooking.repository.CustomerRepository;

/**
 * Implementation of the IDriverRegistrationService interface.
 * 
 * This service handles the business logic for registering new drivers into the system.
 * 
 * Core Responsibilities:
 * - Validates uniqueness of username, email, and license number.
 * - Hashes the driver's password securely using PasswordEncoder before saving.
 * - Creates and stores a Driver entity with an initial unverified status.
 * 
 * Workflow:
 * - Check if `username` is already taken; if so, reject registration.
 * - Check if `email` is already registered; if so, reject registration.
 * - Check if `licenceNo` is already registered; if so, reject registration.
 * - Create new Driver entity with data from registration request.
 * - Password is hashed before persistence.
 * - Set verification flag to false because driver registration needs admin approval.
 * - Initialize driver's rating to 0.0 by default.
 * - Save the Driver entity in the repository and return it.
 *
 * Notes:
 * - The email and license existence checks currently retrieve all drivers from the database
 *   and scan in-memory, which might be inefficient for large datasets.
 *   Consider implementing repository methods for existence checks.
 * - Password hashing ensures security of stored credentials.
 */
@Service
public class DriverRegistrationServiceImpl implements IDriverRegistrationService {

    /**
     * Repository to handle CRUD operations for Admin entity.
     * Provides methods to check for existing usernames and emails.
     */
    @Autowired
    private AdminRepository adminRepository;

    /*
     * Repository to handle CRUD operations for Customer entity.
     * Provides methods to check for existing usernames and emails.
     */
    @Autowired
    private CustomerRepository customerRepository;

    /*
     * Repository to handle CRUD operations for Driver entity.
     * Provides methods to check for existing usernames and emails.
     */
    @Autowired
    private DriverRepository driverRepository;

    /**
     * PasswordEncoder to encrypt driver passwords securely (e.g., BCrypt).
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Registers a new driver account using the provided registration request.
     * 
     * This method performs necessary validations to ensure uniqueness of username, email, and license number.
     * It creates a new Driver entity, hashes the password, sets verification status to false, and persist the entity.
     * 
     * Workflow:
     * - Check if username is already taken.
     * - Check if email is already registered.
     * - Check if license number is already registered.
     * - Create new Driver entity.
     * - Hash password before persistence.
     * - Set verification status to false.
     * - Initialize driver rating to 0.0.
     * - Persist the driver entity and return it.
     * 
     * @param request DTO containing driver's registration data
     * @return The saved and persisted Driver entity
     * @throws IllegalArgumentException if username, email, or license number is already taken
     */
    @Override
    public Driver registerDriver(DriverRegistrationRequest request) {

        if (adminRepository.existsByUsername(request.getUsername()) || customerRepository.existsByUsername(request.getUsername()) || driverRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already taken.");
        }

        // Check if email already registered at the database level
        if (adminRepository.existsByEmail(request.getEmail()) || customerRepository.existsByEmail(request.getEmail()) || driverRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        // Check if license number already registered (ignoring case and null)
        boolean licenceExists = driverRepository.findAll().stream()
            .anyMatch(d -> d.getLicenceNo() != null && d.getLicenceNo().equalsIgnoreCase(request.getLicenceNo()));
        if (licenceExists) {
            throw new IllegalArgumentException("Licence number is already registered.");
        }

        // Create new Driver entity and populate fields from request DTO
        Driver driver = new Driver();
        driver.setUsername(request.getUsername());

        // Hash the plain text password before storing for security
        driver.setPassword(passwordEncoder.encode(request.getPassword()));

        driver.setEmail(request.getEmail());
        driver.setAddress(request.getAddress());
        driver.setMobileNumber(request.getMobileNumber());
        driver.setLicenceNo(request.getLicenceNo());

        // Newly registered drivers are not verified immediately; require admin approval
        driver.setVerified(false);

        // Initialize driver rating to 0.0 by default
        driver.setRating(0.0f);

        // Persist the driver entity and return the saved instance
        return driverRepository.save(driver);
    }
}
