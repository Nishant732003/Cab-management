package com.cabbooking.service;

import com.cabbooking.dto.UserSummaryDTO;
import com.cabbooking.model.TripBooking;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for admin-specific data retrieval operations.
 */
public interface IAdminService {

    /**
     * Retrieves a summary list of all customers.
     * @return A list of UserSummaryDTO for all customers.
     */
    List<UserSummaryDTO> getAllCustomers();

    /**
     * Retrieves a summary list of all drivers.
     * @return A list of UserSummaryDTO for all drivers.
     */
    List<UserSummaryDTO> getAllDrivers();

    /**
     * Retrieves all trips taken by a specific driver.
     * @param driverId The ID of the driver.
     * @return A list of trips.
     */
    List<TripBooking> getTripsByDriver(Integer driverId);

    /**
     * Retrieves all trips that occurred on a specific date.
     * @param date The date to search for.
     * @return A list of trips.
     */
    List<TripBooking> getTripsByDate(LocalDate date);
}