package com.cabbooking.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cabbooking.model.Cab;
import com.cabbooking.repository.CabRepository;

/**
 * Implementation of the ICabService interface.
 *
 * Main Responsibilities:
 * - Provides the business logic for all cab management operations (CRUD).
 * - Interacts with the CabRepository to perform database operations.
 * - Handles validation such as checking for the existence of a cab before an update or delete operation.
 *
 * Workflow:
 * - This service is injected into controllers (like AdminController) that need to manage the cab fleet.
 * - It uses the autowired CabRepository to abstract away the database interaction details.
 */
@Service
public class CabServiceImpl implements ICabService {

    // Repository for handling database operations for Cab entities.
    @Autowired
    private CabRepository cabRepository;

    /**
     * Inserts a new cab into the database.
     *
     * @param cab The Cab object to be saved.
     * @return The saved Cab entity, including its auto-generated ID.
     */
    @Override
    public Cab insertCab(Cab cab) {
        return cabRepository.save(cab);
    }

    /**
     * Updates the details of an existing cab.
     *
     * Workflow:
     * - First, it finds the existing cab by its ID.
     * - If the cab is found, it saves the updated cab object.
     * - If the cab is not found, it throws an IllegalArgumentException.
     *
     * @param cab The Cab object with the updated information.
     * @return The updated Cab entity after it has been saved.
     * @throws IllegalArgumentException if no cab with the given ID exists.
     */
    @Override
    public Cab updateCab(Cab cab) {
        // findById returns an Optional, which prevents NullPointerExceptions
        return cabRepository.findById(cab.getCabId())
                .map(existingCab -> cabRepository.save(cab))
                .orElseThrow(() -> new IllegalArgumentException("Cab with id " + cab.getCabId() + " not found"));
    }

    /**
     * Deletes a cab from the database by its ID.
     *
     * Workflow:
     * - It finds the cab by its ID to ensure it exists.
     * - If found, the cab is deleted.
     * - If not found, an IllegalArgumentException is thrown.
     *
     * @param cabId The unique ID of the cab to delete.
     * @return The Cab object that was deleted.
     * @throws IllegalArgumentException if no cab with the given ID exists.
     */
    @Override
    public Cab deleteCab(int cabId) {
        Cab cab = cabRepository.findById(cabId)
                .orElseThrow(() -> new IllegalArgumentException("Cab with id " + cabId + " not found"));
        cabRepository.delete(cab);
        return cab;
    }

    /**
     * Retrieves a list of all cabs that match a specific car type.
     *
     * @param carType The car type to filter by (e.g., "Sedan", "SUV").
     * @return A list of Cab entities matching the specified type.
     */
    @Override
    public List<Cab> viewCabsOfType(String carType) {
        return cabRepository.findByCarType(carType);
    }

    /**
     * Counts the number of cabs that match a specific car type.
     *
     * @param carType The car type to count.
     * @return The total number of cabs of the specified type.
     */
    @Override
    public int countCabsOfType(String carType) {
        return cabRepository.findByCarType(carType).size();
    }
}