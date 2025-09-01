package com.cabbooking.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.cabbooking.dto.CabUpdateRequest;
import com.cabbooking.dto.FareEstimateResponse;
import com.cabbooking.model.Cab;

/**
 * Service interface for cab management operations.
 */
public interface ICabService {

    /**
     * Updates the details of the cab associated with a specific driver.
     * @param driverId The ID of the driver whose cab is to be updated.
     * @param request The DTO containing the new cab details.
     * @return The updated Cab entity.
     */
    Cab updateCabDetails(int driverId, CabUpdateRequest request);

    /**
     * Retrieves a list of all cabs that match a given car type.
     *
     * @param carType The category of the car to search for (e.g., "Sedan", "SUV").
     * @return A list of Cab entities that match the specified type.
     */
    List<Cab> getCabsOfType(String carType);

    /**
     * Calculates fare estimates for all available and nearby cab types for a given distance and location.
     *
     * @param distance The distance of the trip in kilometers.
     * @param fromLocationLat The from location's latitude.
     * @param fromLocationLng The from location's longitude.
     * @return A list of FareEstimateResponse DTOs, one for each available and nearby car type.
     */
    List<FareEstimateResponse> getAllFareEstimates(float distance, double fromLocationLat, double fromLocationLng);

    /**
     * Retrieves a single cab by its unique ID.
     *
     * @param cabId The ID of the cab to retrieve.
     * @return An Optional containing the Cab if found, otherwise empty.
     */
    Optional<Cab> getCabById(int cabId);

    /*
     * Retrieves a list of all cabs.
     *
     * @return A list of all cabs.
     */
    List<Cab> getAllCabs();

    /*
     * Retrieves a list of all available cabs.
     *
     * @return A list of all available cabs.
     */
    List<Cab> getAllAvailableCabs();

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