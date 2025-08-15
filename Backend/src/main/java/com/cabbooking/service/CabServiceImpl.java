package com.cabbooking.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cabbooking.dto.FareEstimateResponse;
import com.cabbooking.model.Cab;
import com.cabbooking.repository.CabRepository;

/**
 * Implementation of the ICabService interface.
 *
 * Main Responsibilities: - Provides the business logic for all cab management
 * operations (CRUD). - Interacts with the CabRepository to perform database
 * operations. - Handles validation such as checking for the existence of a cab
 * before an update or delete operation.
 *
 * Workflow: - This service is injected into controllers (like AdminController)
 * that need to manage the cab fleet. - It uses the autowired CabRepository to
 * abstract away the database interaction details.
 */
@Service
public class CabServiceImpl implements ICabService {

    // Repository for handling database operations for Cab entities.
    @Autowired
    private CabRepository cabRepository;

    /**
     * Inserts a new cab into the database and sets its availability to true by
     * default.
     *
     * @param cab The Cab object to be saved.
     * @return The saved Cab entity, including its auto-generated ID and default
     * availability.
     */
    @Override
    public Cab insertCab(Cab cab) {
        // Ensure that any new cab is marked as available upon creation.
        cab.setIsAvailable(true);
        return cabRepository.save(cab);
    }

    /**
     * Updates an existing cab with only the provided fields, but only if the
     * cab is currently available.
     *
     * Workflow: - Finds the existing cab by its ID. - Throws an exception if
     * the cab is not found. - **Checks if the cab is available. If it's on a
     * trip (isAvailable=false), it throws an exception.** - If available, it
     * updates only the non-null fields from the incoming request. - Saves the
     * modified cab object back to the database.
     *
     * @param cabRequest The Cab object with the fields to be updated.
     * @return The updated and saved Cab entity.
     * @throws IllegalArgumentException if no cab with the given ID exists.
     * @throws IllegalStateException if the cab is currently in use and cannot
     * be updated.
     */
    @Override
    public Cab updateCab(Cab cabRequest) {
        // Find the existing cab from the database
        Cab existingCab = cabRepository.findById(cabRequest.getCabId())
                .orElseThrow(() -> new IllegalArgumentException("Cab with id " + cabRequest.getCabId() + " not found"));

        // Ensure the cab is not on an active trip
        if (!existingCab.getIsAvailable()) {
            throw new IllegalStateException("Cannot update a cab that is currently on a trip.");
        }

        // Update only the fields that are provided in the request
        if (cabRequest.getCarType() != null) {
            existingCab.setCarType(cabRequest.getCarType());
        }
        if (cabRequest.getPerKmRate() != null) {
            existingCab.setPerKmRate(cabRequest.getPerKmRate());
        }

        // Note: It's generally good practice not to allow changing availability directly via this endpoint.
        // Availability should be managed by the trip booking and completion logic.
        // However, if an admin needs a manual override, you can keep this line.
        if (cabRequest.getIsAvailable() != null) {
            existingCab.setIsAvailable(cabRequest.getIsAvailable());
        }

        // Save the updated entity
        return cabRepository.save(existingCab);
    }

    /**
     * Deletes a cab from the database by its ID.
     *
     * Workflow: - It finds the cab by its ID to ensure it exists. - If the cab
     * is in use, it throws an exception. - If found and available, the cab is
     * deleted.
     *
     * @param cabId The unique ID of the cab to delete.
     * @return The Cab object that was deleted.
     * @throws IllegalArgumentException if no cab with the given ID exists.
     * @throws IllegalStateException if the cab is currently in use and cannot
     * be deleted.
     */
    @Override
    public Cab deleteCab(int cabId) {
        Cab cab = cabRepository.findById(cabId)
                .orElseThrow(() -> new IllegalArgumentException("Cab with id " + cabId + " not found"));

        if (!cab.getIsAvailable()) {
            throw new IllegalStateException("Cannot delete a cab that is currently on a trip.");
        }

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

    @Override
    public List<FareEstimateResponse> getAllFareEstimates(float distance) {
        // 1. Find all available cabs
        List<Cab> availableCabs = cabRepository.findAll().stream()
                .filter(Cab::getIsAvailable)
                .collect(Collectors.toList());

        // 2. Group the cabs by their car type
        Map<String, List<Cab>> cabsByType = availableCabs.stream()
                .collect(Collectors.groupingBy(Cab::getCarType));

        // 3. For each car type, calculate the min/max fare and create a response object
        return cabsByType.entrySet().stream()
                .map(entry -> {
                    String carType = entry.getKey();
                    List<Cab> cabs = entry.getValue();

                    float minRate = cabs.stream()
                            .map(Cab::getPerKmRate)
                            .min(Float::compare)
                            .orElse(0.0f);

                    float maxRate = cabs.stream()
                            .map(Cab::getPerKmRate)
                            .max(Float::compare)
                            .orElse(0.0f);

                    return new FareEstimateResponse(carType, minRate * distance, maxRate * distance);
                })
                .collect(Collectors.toList());
    }

    /**
     * Finds and returns a cab by its unique identifier.
     *
     * @param cabId The ID of the cab to find.
     * @return An Optional<Cab> which contains the cab if it exists.
     */
    @Override
    public Optional<Cab> viewCab(int cabId) {
        return cabRepository.findById(cabId);
    }
}
