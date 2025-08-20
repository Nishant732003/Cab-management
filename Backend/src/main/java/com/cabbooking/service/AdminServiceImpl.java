package com.cabbooking.service;

import com.cabbooking.dto.UserSummaryDTO;
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
 * 
 * This service provides methods to retrieve customer, driver, and trip data for the admin dashboard.
 * 
 * Dependencies:
 * - CustomerRepository for accessing customer data.
 * - DriverRepository for accessing driver data.
 * - TripBookingRepository for accessing trip data.
 */
@Service
public class AdminServiceImpl implements IAdminService {

    /*
     * Repository to handle CRUD operations for customer data.
     * Provides access to customer data for the admin dashboard.
     */
    @Autowired
    private CustomerRepository customerRepository;

    /*
     * Repository to handle CRUD operations for driver data.
     * Provides access to driver data for the admin dashboard.
     */
    @Autowired
    private DriverRepository driverRepository;

    /*
     * Repository to handle CRUD operations for trip data.
     * Provides access to trip data for the admin dashboard.
     */
    @Autowired
    private TripBookingRepository tripBookingRepository;

    /*
     * Retrieves a list of customer summaries for the admin dashboard.
     * 
     * Workflow:
     * - Fetches all customers from the customer repository.
     * - Maps each customer to a UserSummaryDTO, including the customer's ID, username, and email.
     * - Returns the list of customer summaries as a List<UserSummaryDTO>.
     * 
     * @return A list of customer summaries for the admin dashboard.
     */
    @Override
    public List<UserSummaryDTO> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(customer -> new UserSummaryDTO(customer.getId(), customer.getUsername(), customer.getEmail()))
                .collect(Collectors.toList());
    }

    /*
     * Retrieves a list of driver summaries for the admin dashboard.
     * 
     * Workflow:
     * - Fetches all drivers from the driver repository.
     * - Maps each driver to a UserSummaryDTO, including the driver's ID, username, and email.
     * - Returns the list of driver summaries as a List<UserSummaryDTO>.
     * 
     * @return A list of driver summaries for the admin dashboard.
     */
    @Override
    public List<UserSummaryDTO> getAllDrivers() {
        return driverRepository.findAll()
                .stream()
                .map(driver -> new UserSummaryDTO(driver.getId(), driver.getUsername(), driver.getEmail()))
                .collect(Collectors.toList());
    }

    /*
     * Retrieves a list of trip bookings for the admin dashboard.
     * 
     * Workflow:
     * - Fetches all trip bookings from the trip booking repository.
     * - Returns the list of trip bookings as a List<TripBooking>.
     * 
     * @return A list of trip bookings for the admin dashboard.
     */
    @Override
    public List<TripBooking> getTripsByCab(Integer cabId) {
        return tripBookingRepository.findByCab_CabId(cabId);
    }

    /*
     * Retrieves a list of trip bookings for a specific date.
     * 
     * Workflow:
     * - Converts the LocalDate to a LocalDateTime for the start of the day.
     * - Converts the LocalDate to a LocalDateTime for the end of the day.
     * - Fetches all trip bookings within the specified date range from the trip booking repository.
     * - Returns the list of trip bookings as a List<TripBooking>.
     * 
     * @param date The date for which to retrieve trip bookings.
     * @return A list of trip bookings for the specified date.
     */
    @Override
    public List<TripBooking> getTripsByDate(LocalDate date) {
        // Define the start and end of the given day
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return tripBookingRepository.findByFromDateTimeBetween(startOfDay, endOfDay);
    }
}