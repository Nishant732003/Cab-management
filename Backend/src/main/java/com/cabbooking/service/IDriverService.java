package com.cabbooking.service;

import java.util.List;

import com.cabbooking.model.Driver;

/**
 * Service interface for driver management operations.
 *
 * Main Responsibilities:
 * - Defines the contract for all business logic related to drivers.
 * - Abstracts the implementation for viewing drivers and managing their verification status.
 *
 * Workflow:
 * - Implementations of this interface are injected into controllers that handle driver management.
 * - It provides a clear set of operations that an Admin can perform on Driver entities.
 */
public interface IDriverService {

    /**
     * Retrieves a list of the best-performing drivers based on their rating.
     *
     * @return A list of Driver entities considered to be "best drivers" (e.g., rating >= 4.5).
     */
    List<Driver> viewBestDrivers();

    /**
     * Retrieves a list of all drivers who have registered but are not yet verified.
     *
     * @return A list of unverified Driver entities.
     */
    List<Driver> viewUnverifiedDrivers();

    /**
     * Verifies a driver's account, allowing them to be assigned to trips.
     *
     * @param driverId The unique ID of the driver to be verified.
     * @return The updated Driver entity with its 'verified' status set to true.
     */
    Driver verifyDriver(int driverId);
}