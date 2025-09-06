package com.cabbooking.service;

// import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cabbooking.dto.CustomerRegistrationRequest;
import com.cabbooking.model.Customer;
import com.cabbooking.repository.CustomerRepository;
import com.cabbooking.repository.AdminRepository;
import com.cabbooking.repository.DriverRepository;

/**
 * Implementation of the ICustomerRegistrationService interface.
 * 
 * This service manages the business logic related to registering new Customer accounts.
 * 
 * Responsibilities include:
 * - Validating that the username and email are unique.
 * - Hashing the customer's password securely before persisting.
 * - Creating and saving a new Customer entity from the registration data.
 * 
 * This class is annotated with @Service, making it a Spring-managed component that
 * can be injected into controllers or other services.
 */
@Service
public class CustomerRegistrationServiceImpl implements ICustomerRegistrationService {

    /**
     * Repository to handle CRUD operations for Admin, Customer and Driver entities.
     * Provides methods to check for existing usernames and emails.
     */
    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private CustomerRepository customerRepository;

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
     * 1. Check if the username already exists in the system to prevent duplicates.
     *    - If username is taken, throws IllegalArgumentException with appropriate message.
     * 2. Check if the email is already registered (case insensitive).
     *    - Fetches all customers and searches in-memory for email match.
     *    - Throws IllegalArgumentException if email already used.
     *    - (Note: This approach may not scale well; a dedicated query method is recommended.)
     * 3. Creates a new Customer entity and sets its fields based on the request DTO.
     * 4. Password is hashed before setting it on the entity to increase security.
     * 5. Saves the Customer entity in the repository (database).
     * 6. Returns the persisted Customer object.
     * 
     * @param request CustomerRegistrationRequest DTO containing user input.
     * @return The saved Customer entity with generated ID.
     * @throws IllegalArgumentException When username or email is already taken.
     */
    @Override
    public Customer registerCustomer(CustomerRegistrationRequest request) {
        // // Validate username uniqueness
        // Optional<Customer> existingUserByUsername = Optional.ofNullable(
        //     customerRepository.findByUsername(request.getUsername())
        // );
        // if (existingUserByUsername.isPresent()) {
        //     throw new IllegalArgumentException("Username is already taken.");
        // }

        if (adminRepository.existsByUsername(request.getUsername()) || customerRepository.existsByUsername(request.getUsername()) || driverRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already taken.");
        }

        // Validate email uniqueness at the database level
        if (adminRepository.existsByEmail(request.getEmail()) || customerRepository.existsByEmail(request.getEmail()) || driverRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        // Create new Customer entity and populate fields from request
        Customer customer = new Customer();
        customer.setUsername(request.getUsername());
customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        // Securely hash the plaintext password before saving
        customer.setPassword(passwordEncoder.encode(request.getPassword()));

        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());
        customer.setMobileNumber(request.getMobileNumber());

        // Persist the Customer entity to the database
        return customerRepository.save(customer);
    }
}
