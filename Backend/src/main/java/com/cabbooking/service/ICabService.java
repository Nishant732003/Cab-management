package com.cabbooking.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.cabbooking.dto.FareEstimateResponse;
import com.cabbooking.model.Cab;

/**
 * Service interface for cab management operations.
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
    List<Cab> viewAllCabs();
    List<Cab> viewAllAvailableCabs();

    /**
     * Uploads an image for a specific cab.
     * @param cabId The ID of the cab.
     * @param file The image file to upload.
     * @return The updated Cab object with the new image URL.
     * @throws IOException if the file upload fails.
     */
    Cab uploadImage(int cabId, MultipartFile file) throws IOException;

    /**
     * Removes the image for a specific cab.
     * @param cabId The ID of the cab.
     * @return The updated Cab object with the image URL removed.
     * @throws IOException if the file deletion fails.
     */
    Cab removeImage(int cabId) throws IOException;
}