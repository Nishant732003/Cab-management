package com.cabbooking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cabbooking.dto.DriverRegistrationRequest;
import com.cabbooking.model.Cab;
import com.cabbooking.model.Driver;
import com.cabbooking.repository.AdminRepository;
import com.cabbooking.repository.CustomerRepository;
import com.cabbooking.repository.DriverRepository;

/**
 * Implementation of the IDriverRegistrationService interface.
 * 
 * This service handles the business logic for registering new drivers into the system.
 * 
 * Main Responsibilities:
 * - Validates uniqueness of username, email, and license number.
 * - Hashes the driver's password securely using PasswordEncoder before saving.
 * - Creates and stores a Driver entity with an initial unverified status.
 * - Creates and stores a Cab entity associated with the driver.
 *
 * Dependencies:
 * - AdminRepository for database persistence and uniqueness checks.
 * - CustomerRepository for customer uniqueness checks.
 * - DriverRepository for driver uniqueness checks.
 * - PasswordEncoder for securely hashing driver passwords.
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
     * Workflow:
     * - Check if username is already taken by querying all repositories.
     * - Check if email is already registered by querying all repositories.
     * - Check if license number is already registered by querying the driver repository.
     * - Create new Driver entity.
     * - Hash password before persistence.
     * - Set verification status to false.
     * - Initialize driver rating to 0.0.
     * - Persist the driver entity and return it.
     * - Create a new, empty Cab instance.
     * - Establish the bidirectional link between driver and cab.
     * - Persist the cab entity.
     * - Return the saved driver entity.
     * 
     * @param request DTO containing driver's registration data
     * @return The saved and persisted Driver entity
     * @throws IllegalArgumentException if username, email, or license number is already taken
     */
    @Override
    public Driver registerDriver(DriverRegistrationRequest request) {

        // Check if username already exists at the database level
        if (adminRepository.existsByUsername(request.getUsername()) 
        || customerRepository.existsByUsername(request.getUsername()) 
        || driverRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already taken.");
        }

        // Check if email already registered at the database level
        if (adminRepository.existsByEmail(request.getEmail()) 
        || customerRepository.existsByEmail(request.getEmail()) 
        || driverRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        // Check if license number already exists at the database level
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

        // Set the "verified" flag to false
        driver.setVerified(false);

        // Initialize driver rating to 0.0 by default
        driver.setRating(request.getRating());
        driver.setTotalRatings(request.getTotalRatings());
        driver.setLatitude(request.getLatitude());
        driver.setLongitude(request.getLongitude());

        // Create a new, empty Cab instance
        Cab cab = new Cab();
        
        // Establish the bidirectional link
        cab.setDriver(driver); // Link cab to the driver
        driver.setCab(cab);    // Link driver to the cab

        // Save the driver. Because of CascadeType.ALL, the associated cab will be saved automatically.
        return driverRepository.save(driver);
    }
}
