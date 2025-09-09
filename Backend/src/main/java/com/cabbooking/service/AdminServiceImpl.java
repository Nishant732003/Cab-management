package com.cabbooking.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cabbooking.dto.UserSummaryDTO;
import com.cabbooking.model.Customer;
import com.cabbooking.model.Driver;
import com.cabbooking.repository.CustomerRepository;
import com.cabbooking.repository.DriverRepository;

/**
 * Implementation of the IAdminService for admin data operations.
 * 
 * Provides methods to retrieve customer and driver summaries.
 * 
 * Main Responsibilities:
 * - Fetch all customers and drivers from the database.
 * - Convert entities to UserSummaryDTOs for admin dashboard display.
 * - Log operations for monitoring and debugging.
 * - Handle potential null values gracefully.
 * - Ensure data integrity and consistency in the returned summaries.
 * - Facilitate easy integration with other services or components in the application.
 * 
 * Dependencies:
 * - CustomerRepository: For accessing customer data.
 * - DriverRepository: For accessing driver data.
 */
@Service
public class AdminServiceImpl implements IAdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

    /*
     * Provides access to customer data in the database.
     */
    @Autowired
    private CustomerRepository customerRepository;

    /*
     * Provides access to driver data in the database.
     */
    @Autowired
    private DriverRepository driverRepository;

    /**
     * Retrieves a list of customer summaries for the admin dashboard.
     * 
     * Workflow:
     * - Fetch all customers from the repository.
     * - Map each Customer entity to a UserSummaryDTO.
     * - Collect and return the list of UserSummaryDTOs.
     * 
     * @return List of UserSummaryDTO representing all customers.
     * @throws RuntimeException if any error occurs during data retrieval or mapping.
     */
    @Override
    public List<UserSummaryDTO> getAllCustomers() {
        logger.info("Fetching all customers for admin.");
        return customerRepository.findAll()
                .stream()
                .map(this::mapCustomerToUserSummaryDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a list of driver summaries for the admin dashboard.
     * 
     * Workflow:
     * - Fetch all drivers from the repository.
     * - Map each Driver entity to a UserSummaryDTO.
     * - Collect and return the list of UserSummaryDTOs.
     * 
     * @return List of UserSummaryDTO representing all drivers.
     * @throws RuntimeException if any error occurs during data retrieval or mapping.
     */
    @Override
    public List<UserSummaryDTO> getAllDrivers() {
        logger.info("Fetching all drivers for admin.");
        return driverRepository.findAll()
                .stream()
                .map(this::mapDriverToUserSummaryDTO)
                .collect(Collectors.toList());
    }
    
    /* ==============
     * HELPER METHODS
     * ==============
     */

    /**
     * Helper method to convert a Customer entity into a UserSummaryDTO.
     */
    private UserSummaryDTO mapCustomerToUserSummaryDTO(Customer customer) {
        UserSummaryDTO userSummary = new UserSummaryDTO();
        userSummary.setUserId(customer.getId());
        userSummary.setUsername(customer.getUsername());
        userSummary.setEmail(customer.getEmail());
        userSummary.setFirstName(customer.getFirstName());
        userSummary.setLastName(customer.getLastName());
        userSummary.setMobileNumber(customer.getMobileNumber());
        return userSummary;
    }
    
    /**
     * Helper method to convert a Driver entity into a UserSummaryDTO.
     */
    private UserSummaryDTO mapDriverToUserSummaryDTO(Driver driver) {
        Double ratingAsDouble = (driver.getRating() != null) ? driver.getRating().doubleValue() : 0.0;

        return new UserSummaryDTO(
            driver.getId(),
            driver.getUsername(),
            driver.getFirstName(),
            driver.getLastName(),
            driver.getEmail(),
            driver.getMobileNumber(),
            ratingAsDouble, 
            driver.getLicenceNo(),
            driver.getVerified()
        );
    }
}