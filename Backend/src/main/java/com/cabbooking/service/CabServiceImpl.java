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

import com.cabbooking.dto.FareEstimateResponse;
import com.cabbooking.model.Cab;
import com.cabbooking.repository.CabRepository;

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
 * Workflow: 
 * - This service is injected into controllers (like AdminController)
 * that need to manage the cab fleet. 
 * - It uses the autowired CabRepository to
 * abstract away the database interaction details.
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
     * Service to handle file uploads
     * Provides methods for file uploads
     */
    @Autowired
    private IFileUploadService fileUploadService;

    /**
     * Inserts a new cab into the database and sets its availability to true by
     * default.
     * 
     * Workflow: 
     * - Creates a new Cab object from the provided request. 
     * - Sets the availability to true by default. 
     * - Saves the cab object to the database.
     *
     * @param cab The Cab object to be saved.
     * @return The saved Cab entity, including its auto-generated ID and default
     * availability.
     */
    @Override
    public Cab insertCab(Cab cab) {
        if (cabRepository.existsByNumberPlate(cab.getNumberPlate())) {
            throw new IllegalArgumentException("A cab with number plate '" + cab.getNumberPlate() + "' already exists.");
        }
        // Ensure that any new cab is marked as available upon creation.
        cab.setIsAvailable(true);
        return cabRepository.save(cab);
    }

    /**
     * Updates an existing cab with only the provided fields, but only if the
     * cab is currently available.
     *
     * Workflow: 
     * - Finds the existing cab by its ID. 
     * - Throws an exception if the cab is not found. 
     * - Checks if the cab is available. If it's on a trip (isAvailable=false), it throws an exception.
     * - If available, it updates only the non-null fields from the incoming request.
     * - Saves the modified cab object back to the database.
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

        if (cabRequest.getNumberPlate() != null && !cabRequest.getNumberPlate().equals(existingCab.getNumberPlate())) {
            if (cabRepository.existsByNumberPlate(cabRequest.getNumberPlate())) {
                throw new IllegalArgumentException("A cab with number plate '" + cabRequest.getNumberPlate() + "' already exists.");
            }
            existingCab.setNumberPlate(cabRequest.getNumberPlate());
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
     * Workflow: 
     * - It finds the cab by its ID to ensure it exists. 
     * - If the cab is in use, it throws an exception. 
     * - If found and available, the cab is deleted.
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
     * Workflow: 
     * - Calls the repository to find cabs by their car type.
     * - Returns the resulting list of cabs.
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
     * Workflow:
     * - Calls the repository to find cabs by their car type.
     * - Returns the size of the resulting list, which is the count.
     * 
     * @param carType The car type to count.
     * @return The total number of cabs of the specified type.
     */
    @Override
    public int countCabsOfType(String carType) {
        return cabRepository.findByCarType(carType).size();
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
    public Optional<Cab> viewCab(int cabId) {
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
    public List<Cab> viewAllCabs() {
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
    public List<Cab> viewAllAvailableCabs() {
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
        // 1. Find the cab
        Cab cab = cabRepository.findById(cabId)
                .orElseThrow(() -> new IllegalArgumentException("Cab with id " + cabId + " not found"));

        // If an old image exists, delete it first before uploading the new one
        if (cab.getImageUrl() != null && !cab.getImageUrl().isEmpty()) {
            removeImageFile(cab.getImageUrl());
        }

        // 2. Upload the new file and get its unique filename
        String fileName = fileUploadService.uploadFile(file);
        String fileApiUrl = "/api/files/" + fileName;

        // 3. Set the new URL on the cab and save
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
        // 1. Find the cab
        Cab cab = cabRepository.findById(cabId)
                .orElseThrow(() -> new IllegalArgumentException("Cab with id " + cabId + " not found"));

        // 2. Delete the physical file
        removeImageFile(cab.getImageUrl());

        // 3. Clear the URL from the cab's record and save
        cab.setImageUrl(null);
        return cabRepository.save(cab);
    }

    /**
     * Helper method to safely delete an image file.
     */
    private void removeImageFile(String imageUrl) throws IOException {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            String fileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
            fileUploadService.deleteFile(fileName);
        }
    }
}
