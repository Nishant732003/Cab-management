package com.cabbooking.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cabbooking.model.Driver;
import com.cabbooking.repository.DriverRepository;

/**
 * Implementation of the IDriverService interface.
 *
 * Main Responsibilities:
 * - Provides the business logic for all driver-related management operations.
 * - Interacts with the DriverRepository to perform database queries and updates.
 * - Handles tasks such as viewing top-rated drivers and managing their verification status.
 *
 * Workflow:
 * - This service is injected into controllers, primarily the AdminController, to expose driver management functionality.
 * - It uses the DriverRepository to fetch and manipulate driver data from the database.
 */
@Service
public class DriverServiceImpl implements IDriverService {

    // Repository for handling database operations for Driver entities.
    @Autowired
    private DriverRepository driverRepository;

    /**
     * Retrieves a list of the best-performing drivers.
     *
     * Workflow:
     * - Fetches all drivers from the repository.
     * - Filters the list to include only those with a rating of 4.5 or higher, as per business rules.
     * - This in-memory filtering is suitable for a moderate number of drivers.
     *
     * @return A list of Driver entities considered to be "best drivers".
     */
    @Override
    public List<Driver> viewBestDrivers() {
        // As per the PDF, best drivers have a rating of 4.5 or higher
        return driverRepository.findAll().stream()
                .filter(driver -> driver.getRating() != null && driver.getRating() >= 4.5f)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a list of all drivers who are awaiting admin verification.
     *
     * Workflow:
     * - Fetches all drivers from the repository.
     * - Filters the list to include only drivers whose 'verified' status is false.
     *
     * @return A list of unverified Driver entities.
     */
    @Override
    public List<Driver> viewUnverifiedDrivers() {
        return driverRepository.findAll().stream()
                .filter(driver -> driver.getVerified() != null && !driver.getVerified())
                .collect(Collectors.toList());
    }

    /**
     * Verifies a driver's account, marking them as active and eligible for trips.
     *
     * Workflow:
     * - Finds the driver by their unique ID.
     * - Throws an exception if no driver is found.
     * - Sets the 'verified' flag to true.
     * - Saves the updated driver entity back to the database.
     *
     * @param driverId The unique ID of the driver to verify.
     * @return The updated and verified Driver entity.
     * @throws IllegalArgumentException if no driver with the given ID is found.
     */
    @Override
    public Driver verifyDriver(int driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found with id: " + driverId));
        
        driver.setVerified(true);
        return driverRepository.save(driver);
    }
}