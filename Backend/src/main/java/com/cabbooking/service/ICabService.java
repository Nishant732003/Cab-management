package com.cabbooking.service;

import java.util.List;
import java.util.Optional;

import com.cabbooking.dto.FareEstimateResponse;
import com.cabbooking.model.Cab;

/**
 * Service interface for cab management operations.
 *
 * Main Responsibilities:
 * - Defines the contract for all business logic related to the cab fleet.
 * - Abstracts the implementation details for creating, updating, deleting, and viewing cabs.
 *
 * Workflow:
 * - Implementations of this interface will be injected into controllers that handle cab management.
 * - It provides a clear set of operations that can be performed on Cab entities.
 */
public interface ICabService {

    /**
     * Creates and persists a new cab in the system.
     *
     * @param cab The new Cab object to be saved.
     * @return The persisted Cab entity, including its unique generated ID.
     */
    Cab insertCab(Cab cab);

    /**
     * Updates the details of an existing cab.
     *
     * @param cab The Cab object containing the updated information.
     * @return The updated Cab entity after it has been saved to the database.
     */
    Cab updateCab(Cab cab);

    /**
     * Deletes a cab from the system using its unique ID.
     *
     * @param cabId The ID of the cab to be deleted.
     * @return The Cab object that was successfully deleted.
     */
    Cab deleteCab(int cabId);

    /**
     * Retrieves a list of all cabs that match a given car type.
     *
     * @param carType The category of the car to search for (e.g., "Sedan", "SUV").
     * @return A list of Cab entities that match the specified type.
     */
    List<Cab> viewCabsOfType(String carType);

    /**
     * Counts the total number of cabs of a specific type.
     *
     * @param carType The category of the car to count.
     * @return The total number of cabs found for the given type.
     */
    int countCabsOfType(String carType);

    /**
     * Calculates fare estimates for all available car types for a given distance.
     *
     * @param distance The distance of the trip in kilometers.
     * @return A list of FareEstimateResponse DTOs, one for each available car type.
     */
    List<FareEstimateResponse> getAllFareEstimates(float distance);

    /**
     * Retrieves a single cab by its unique ID.
     *
     * @param cabId The ID of the cab to retrieve.
     * @return An Optional containing the Cab if found, otherwise empty.
     */
    Optional<Cab> viewCab(int cabId);
}