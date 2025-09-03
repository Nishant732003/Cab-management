package com.cabbooking.service;

import com.cabbooking.dto.UserSummaryDTO;
import com.cabbooking.model.Customer;
import com.cabbooking.model.Driver;
import com.cabbooking.model.TripBooking;
import com.cabbooking.repository.CustomerRepository;
import com.cabbooking.repository.DriverRepository;
import com.cabbooking.repository.TripBookingRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the IAdminService for admin data operations.
 */
@Service
public class AdminServiceImpl implements IAdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private TripBookingRepository tripBookingRepository;

    /**
     * Retrieves a list of customer summaries for the admin dashboard.
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
     */
    @Override
    public List<UserSummaryDTO> getAllDrivers() {
        logger.info("Fetching all drivers for admin.");
        return driverRepository.findAll()
                .stream()
                .map(this::mapDriverToUserSummaryDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Converts a Customer entity into a UserSummaryDTO.
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
     */
    private UserSummaryDTO mapDriverToUserSummaryDTO(Driver driver) {
        String displayName = driver.getUsername() != null 
            ? driver.getUsername().substring(0, 1).toUpperCase() + driver.getUsername().substring(1)
            : "N/A";
        
        Double ratingAsDouble = (driver.getRating() != null) ? driver.getRating().doubleValue() : 0.0;

        return new UserSummaryDTO(
            driver.getId(),
            driver.getUsername(),
            driver.getEmail(),
            displayName,
            driver.getMobileNumber(),
            ratingAsDouble, 
            driver.getLicenceNo(),
            driver.getVerified()
        );
    }

    @Override
    public List<TripBooking> getTripsByCab(Integer cabId) {
        logger.info("Fetching trips for cab with ID: {}", cabId);
        return tripBookingRepository.findByCab_CabId(cabId);
    }
    
    /**
     * Retrieves all trips for a given driver.
     * @param driverId The ID of the driver.
     * @return A list of trips for that driver.
     */
    @Override
    public List<TripBooking> getTripsByDriver(Integer driverId) {
        logger.info("Fetching trips for driver with ID: {}", driverId);
        return tripBookingRepository.findByDriver_Id(driverId);
    }

    @Override
    public List<TripBooking> getTripsByDate(LocalDate date) {
        logger.info("Fetching trips for date: {}", date);
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return tripBookingRepository.findByFromDateTimeBetween(startOfDay, endOfDay);
    }
}
