package com.cabbooking.service;

import com.cabbooking.dto.UserSummaryDTO;
import com.cabbooking.model.Customer;
import com.cabbooking.model.Driver;
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

    /* (Your existing comments are preserved) */
    @Autowired
    private CustomerRepository customerRepository;

    /* (Your existing comments are preserved) */
    @Autowired
    private DriverRepository driverRepository;

    /* (Your existing comments are preserved) */
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
                .map(this::mapCustomerToUserSummaryDTO)
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
                .map(this::mapDriverToUserSummaryDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Converts a Customer entity into a UserSummaryDTO.
     * (Your existing comments are preserved)
     */
    private UserSummaryDTO mapCustomerToUserSummaryDTO(Customer customer) {
        String displayName = customer.getUsername() != null 
            ? customer.getUsername().substring(0, 1).toUpperCase() + customer.getUsername().substring(1)
            : "N/A";

        return new UserSummaryDTO(
            customer.getId(),
            customer.getUsername(),
            customer.getEmail(),
            displayName,
            customer.getMobileNumber()
        );
    }
    
    /**
     * Converts a Driver entity into a UserSummaryDTO.
     * (Your existing comments are preserved)
     */
    private UserSummaryDTO mapDriverToUserSummaryDTO(Driver driver) {
        String displayName = driver.getUsername() != null 
            ? driver.getUsername().substring(0, 1).toUpperCase() + driver.getUsername().substring(1)
            : "N/A";
        
        // FIX: Convert the Float rating to a Double before passing it to the DTO constructor.
        Double ratingAsDouble = (driver.getRating() != null) ? driver.getRating().doubleValue() : 0.0;

        return new UserSummaryDTO(
            driver.getId(),
            driver.getUsername(),
            driver.getEmail(),
            displayName,
            driver.getMobileNumber(),
            ratingAsDouble, // Pass the converted Double value
            driver.getLicenceNo(),
            driver.getVerified()
        );
    }

    /* (Your existing methods and comments are preserved) */
    @Override
    public List<TripBooking> getTripsByCab(Integer cabId) {
        return tripBookingRepository.findByCab_CabId(cabId);
    }

    /* (Your existing methods and comments are preserved) */
    @Override
    public List<TripBooking> getTripsByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return tripBookingRepository.findByFromDateTimeBetween(startOfDay, endOfDay);
    }
}