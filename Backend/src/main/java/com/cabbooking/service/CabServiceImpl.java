package com.cabbooking.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.cabbooking.dto.CabUpdateRequest;
import com.cabbooking.dto.FareEstimateResponse;
import com.cabbooking.model.Cab;
import com.cabbooking.model.Driver;
import com.cabbooking.repository.CabRepository;
import com.cabbooking.repository.DriverRepository;

/**
 * Implementation of the ICabService interface.
 *
 * Main Responsibilities: 
 * - Provides the business logic for all cab management
 * operations (CRUD). 
 * - Interacts with the CabRepository to perform database
 * operations. 
 * - Handles validation such as checking for the existence of a cab
 * before an update or delete operation.
 *
 * Dependencies:
 * - CabRepository for accessing cab data in the database.
 * - DriverRepository for accessing driver data in the database.
 * - IFileUploadService for handling file uploads.
 */
@Service
public class CabServiceImpl implements ICabService {

    /*
     * Repository to interact with the database
     * Provides methods for CRUD operations
     */
    @Autowired
    private CabRepository cabRepository;

    /*
     * Repository to interact with the database
     * Provides methods for CRUD operations
     */
    @Autowired
    private DriverRepository driverRepository;

    /*
     * Service to handle file uploads
     * Provides methods for file uploads
     */
    @Autowired
    private IFileUploadService fileUploadService;

    /*
     * Updates the details of a cab associated with a driver.
     * 
     * Workflow: 
     * - Finds the driver by their ID to ensure they exist. 
     * - Gets the cab associated with this driver. 
     * - Updates the cab's details from the request. 
     * - Saves the updated cab to the database.
     * 
     * @param driverId The ID of the driver whose cab is to be updated.
     * @param request The request object containing the new cab details.
     * @return The updated Cab object
     */
    @Override
    @Transactional
    public Cab updateCabDetails(int driverId, CabUpdateRequest request) {
        // Find the driver to ensure they exist
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Driver with id " + driverId + " not found"));

        // Get the cab associated with this driver
        Cab cabToUpdate = driver.getCab();
        if (cabToUpdate == null) {
            throw new IllegalStateException("No cab is associated with this driver.");
        }

        // Update the cab's details from the request
        cabToUpdate.setNumberPlate(request.getNumberPlate());
        cabToUpdate.setCarType(request.getCarType());
        cabToUpdate.setPerKmRate(request.getPerKmRate());
        cabToUpdate.setIsAvailable(true); // When details are added, make the cab available

        // Save the updated cab
        return cabRepository.save(cabToUpdate);
    }

    /**
     * Retrieves a list of all cabs that match a specific car type.
     * 
     * Workflow: 
     * - Calls the repository to find cabs by their car type.
     * - Returns the resulting list of cabs.
     *
     * @param carType The car type to filter by (e.g., "Sedan", "SUV").
     * @return A list of Cab entities matching the specified type.
     */
    @Override
    public List<Cab> getCabsOfType(String carType) {
        return cabRepository.findByCarType(carType);
    }

    /*
     * Calculates the fare estimates for all available cabs based on the distance.
     * 
     * Workflow:
     * - Retrieves all available cabs from the repository.
     * - Groups the cabs by their car type.
     * - For each car type, calculates the minimum and maximum fares for the given distance.
     * - Creates a FareEstimateResponse object for each car type and returns a list of them.
     * 
     * @param distance The distance in kilometers.
     * @return A list of FareEstimateResponse objects, one for each available car type.
     */
    @Override
    public List<FareEstimateResponse> getAllFareEstimates(float distance) {
        // Find all available cabs
        List<Cab> availableCabs = cabRepository.findAll().stream()
                .filter(Cab::getIsAvailable)
                .collect(Collectors.toList());

        // Group the cabs by their car type
        Map<String, List<Cab>> cabsByType = availableCabs.stream()
                .collect(Collectors.groupingBy(Cab::getCarType));

        // For each car type, calculate the min/max fare and create a response object
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
     * Workflow: 
     * - Calls the repository to find the cab by its ID.
     * - Returns the Optional containing the cab if found, otherwise empty.
     *
     * @param cabId The ID of the cab to find.
     * @return An Optional<Cab> which contains the cab if it exists.
     */
    @Override
    public Optional<Cab> getCabById(int cabId) {
        return cabRepository.findById(cabId);
    }

    /**
     * Retrieves a list of all cabs in the system.
     * 
     * Workflow: 
     * - Calls the repository to find all cabs.
     * - Returns the list of cabs.
     *
     * @return A list of all Cab entities.
     */
    @Override
    public List<Cab> getAllCabs() {
        return cabRepository.findAll();
    }
    
    /**
     * Retrieves a list of all available cabs in the system.
     * 
     * Workflow: 
     * - Calls the repository to find all cabs.
     * - Filters the list to include only available cabs.
     * - Returns the list of available cabs.
     *
     * @return A list of all available Cab entities.
     */
    @Override
    public List<Cab> getAllAvailableCabs() {
        return cabRepository.findAll().stream()
                .filter(Cab::getIsAvailable)
                .collect(Collectors.toList());
    }

    /*
     * Uploads an image file for a cab and updates its URL in the database.
     * 
     * Workflow:
     * - Finds the cab by its ID.
     * - If an old image exists, deletes it.
     * - Uploads the new image file.
     * - Sets the new image URL on the cab and saves it.
     * - Returns the updated cab.
     * 
     * @param cabId The ID of the cab.
     * @param file The image file to upload.
     * @return The updated Cab object with the new image URL.
     * @throws IOException if the file upload fails.
     */
    @Override
    @Transactional
    public Cab uploadImage(int cabId, MultipartFile file) throws IOException {
        // Find the cab
        Cab cab = cabRepository.findById(cabId)
                .orElseThrow(() -> new IllegalArgumentException("Cab with id " + cabId + " not found"));

        // If an old image exists, delete it first before uploading the new one
        if (cab.getImageUrl() != null && !cab.getImageUrl().isEmpty()) {
            removeImageFile(cab.getImageUrl());
        }

        // Upload the new file and get its unique filename
        String fileName = fileUploadService.uploadFile(file);
        String fileApiUrl = "/api/files/" + fileName;

        // Set the new URL on the cab and save
        cab.setImageUrl(fileApiUrl);
        return cabRepository.save(cab);
    }

    /*
     * Removes the image file for a cab and clears its URL in the database.
     * 
     * Workflow:
     * - Finds the cab by its ID.
     * - Deletes the old image file if it exists.
     * - Clears the image URL on the cab and saves it.
     * - Returns the updated cab.
     * 
     * @param cabId The ID of the cab.
     * @return The updated Cab object with the image URL removed.
     * @throws IOException if the file deletion fails.
     */
    @Override
    @Transactional
    public Cab removeImage(int cabId) throws IOException {
        // Find the cab
        Cab cab = cabRepository.findById(cabId)
                .orElseThrow(() -> new IllegalArgumentException("Cab with id " + cabId + " not found"));

        // Delete the physical file
        removeImageFile(cab.getImageUrl());

        // Clear the URL from the cab's record and save
        cab.setImageUrl(null);
        return cabRepository.save(cab);
    }

    /**
     * Helper method to safely delete an image file.
     * 
     * @param imageUrl The URL of the image file to delete.
     * @throws IOException if the file deletion fails.
     */
    private void removeImageFile(String imageUrl) throws IOException {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            String fileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
            fileUploadService.deleteFile(fileName);
        }
    }
}
