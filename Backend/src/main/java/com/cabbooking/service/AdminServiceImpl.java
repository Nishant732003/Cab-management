package com.cabbooking.service;

import com.cabbooking.dto.UserSummaryDTO;
import com.cabbooking.model.Customer; // Import Customer model
import com.cabbooking.model.Driver;   // Import Driver model
import com.cabbooking.model.TripBooking;
import com.cabbooking.repository.CustomerRepository;
import com.cabbooking.repository.DriverRepository;
import com.cabbooking.repository.TripBookingRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the IAdminService for admin data operations.
 * (Your existing comments are preserved)
 */
@Service
public class AdminServiceImpl implements IAdminService {

    /*
     * (Your existing comments are preserved)
     */
    @Autowired
    private CustomerRepository customerRepository;

    /*
     * (Your existing comments are preserved)
     */
    @Autowired
    private DriverRepository driverRepository;

    /*
     * (Your existing comments are preserved)
     */
    @Autowired
    private TripBookingRepository tripBookingRepository;

    /**
     * Retrieves a list of customer summaries for the admin dashboard.
     * (Your existing comments are preserved)
     */
    @Override
    public List<UserSummaryDTO> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::mapCustomerToUserSummaryDTO) // Use a dedicated mapping method
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a list of driver summaries for the admin dashboard.
     * (Your existing comments are preserved)
     */
    @Override
    public List<UserSummaryDTO> getAllDrivers() {
        return driverRepository.findAll()
                .stream()
                .map(this::mapDriverToUserSummaryDTO) // Use a dedicated mapping method
                .collect(Collectors.toList());
    }
    
    // --- FIX: Corrected mapping methods to use getId() ---

    /**
     * Converts a Customer entity into a UserSummaryDTO.
     * This ensures the frontend receives all the data it needs.
     * @param customer The Customer entity from the database.
     * @return A UserSummaryDTO populated with the customer's information.
     */
    private UserSummaryDTO mapCustomerToUserSummaryDTO(Customer customer) {
        // Create a display name from the username as the entity does not have a separate 'name' field.
        String displayName = customer.getUsername() != null 
            ? customer.getUsername().substring(0, 1).toUpperCase() + customer.getUsername().substring(1)
            : "N/A";

        return new UserSummaryDTO(
            customer.getId(),      // FIX: Changed from getUserId() to getId()
            customer.getUsername(),
            customer.getEmail(),
            displayName,           // Populate the name
            customer.getMobileNumber() // Populate the mobile number
        );
    }
    
    /**
     * Converts a Driver entity into a UserSummaryDTO.
     * @param driver The Driver entity from the database.
     * @return A UserSummaryDTO populated with the driver's information.
     */
    private UserSummaryDTO mapDriverToUserSummaryDTO(Driver driver) {
        String displayName = driver.getUsername() != null 
            ? driver.getUsername().substring(0, 1).toUpperCase() + driver.getUsername().substring(1)
            : "N/A";

        return new UserSummaryDTO(
            driver.getId(),        // FIX: Changed from getUserId() to getId()
            driver.getUsername(),
            driver.getEmail(),
            displayName,
            driver.getMobileNumber()
        );
    }


    /*
     * (Your existing methods and comments are preserved)
     */
    @Override
    public List<TripBooking> getTripsByCab(Integer cabId) {
        return tripBookingRepository.findByCab_CabId(cabId);
    }

    /*
     * (Your existing methods and comments are preserved)
     */
    @Override
    public List<TripBooking> getTripsByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return tripBookingRepository.findByFromDateTimeBetween(startOfDay, endOfDay);
    }
}

