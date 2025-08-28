package com.cabbooking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cabbooking.dto.CustomerRegistrationRequest;
import com.cabbooking.model.Customer;
import com.cabbooking.repository.AdminRepository;
import com.cabbooking.repository.CustomerRepository;
import com.cabbooking.repository.DriverRepository;

/**
 * Implementation of the ICustomerRegistrationService interface.
 * 
 * This service manages the business logic related to registering new Customer accounts.
 * 
 * Main Responsibilities:
 * - Validating that the username and email are unique.
 * - Hashing the customer's password securely before persisting.
 * - Creating and saving a new Customer entity from the registration data.
 * 
 * Dependencies:
 * - AdminRepository for database persistence and uniqueness checks.
 * - CustomerRepository for customer uniqueness checks.
 * - DriverRepository for driver uniqueness checks.
 * - PasswordEncoder for securely hashing customer passwords.
 */
@Service
public class CustomerRegistrationServiceImpl implements ICustomerRegistrationService {

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
     * PasswordEncoder provided by Spring Security.
     * Used here for securely hashing passwords before saving.
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Registers a new customer using the provided registration data.
     * 
     * Workflow:
     * - Check if the username already exists by querying all repositories.
     *    - If username is taken, throws IllegalArgumentException with appropriate message.
     * - Check if the email is already registered by querying all repositories.
     *    - If email is already registered, throws IllegalArgumentException with appropriate message.
     * - Creates a new Customer entity and sets its fields based on the request DTO.
     * - Password is hashed before setting it on the entity to increase security.
     * - Saves the Customer entity in the repository (database).
     * - Returns the persisted Customer object.
     * 
     * @param request CustomerRegistrationRequest DTO containing user input.
     * @return The saved Customer entity.
     * @throws IllegalArgumentException When username or email is already taken.
     */
    @Override
    public Customer registerCustomer(CustomerRegistrationRequest request) {

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

        // Create new Customer entity and populate fields from request
        Customer customer = new Customer();
        customer.setUsername(request.getUsername());

        // Securely hash the plaintext password before saving
        customer.setPassword(passwordEncoder.encode(request.getPassword()));

        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());
        customer.setMobileNumber(request.getMobileNumber());

        // Persist the Customer entity to the database
        return customerRepository.save(customer);
    }
}
