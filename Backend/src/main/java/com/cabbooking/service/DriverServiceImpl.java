package com.cabbooking.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
 * Dependencies:
 * - DriverRepository for accessing driver data in the database.
 * - IFileUploadService for handling file uploads.
 */
@Service
public class DriverServiceImpl implements IDriverService {

    /*
     * Repository for accessing driver data.
     */
    @Autowired
    private DriverRepository driverRepository;

    /*
     * Service for handling file uploads.
     */
    @Autowired
    private IFileUploadService fileUploadService;

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
        return driverRepository.findByVerifiedFalse();
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
     * @return A message indicating successful verification.
     * @throws IllegalArgumentException if no driver with the given ID is found.
     */
    @Override
    public String verifyDriver(int driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found with id: " + driverId));
        
        driver.setVerified(true);
        driverRepository.save(driver);
        return "Driver verified successfully";
    }

    /*
     * Uploads a profile photo for a driver and updates their record.
     *
     * Workflow:
     * - Finds the driver by their unique username.
     * - Throws an exception if no driver is found.
     * - Uses the IFileUploadService to upload the file and get its unique filename.
     * - Constructs the API path to serve the file and sets it on the driver's profile.
     * - Saves the updated driver entity back to the database.
     *
     * @param username The username of the driver.
     * @param file The image file to upload.
     * @return The updated Driver object with the new photo URL.
     * @throws IOException if the file upload fails.
     */
    @Override
    public Driver uploadProfilePhoto(String username, MultipartFile file) throws IOException {
        // Find the driver
        Driver driver = driverRepository.findByUsername(username);
        
        // Throw exception if no driver is found
        if (driver == null) {
            throw new IllegalArgumentException("Driver not found...");
        }

        // If an old image exists, delete it first before uploading the new one
        if (driver.getProfilePhotoUrl() != null && !driver.getProfilePhotoUrl().isEmpty()) {
            removeImageFile(driver.getProfilePhotoUrl());
        }

        // Upload the file and get its unique filename
        String fileName = fileUploadService.uploadFile(file);

        // Construct the API path to serve the file and set it on the driver
        String fileApiUrl = "/api/files/" + fileName;
        driver.setProfilePhotoUrl(fileApiUrl);
        
        return driverRepository.save(driver);
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

    /*
     * Removes a profile photo for a driver and updates their record.
     * 
     * Workflow:
     * - Finds the driver by their unique ID.
     * - Throws an exception if no driver is found.
     * - Checks if a photo URL exists.
     * - If one exists, deletes the physical file and clears the URL from the driver's record.
     * - Saves the updated driver entity back to the database.
     *
     * @param driverId The ID of the driver.
     * @return The updated Driver object with the photo URL removed.
     * @throws IOException if the file deletion fails.
     */
    @Override
    @Transactional
    public Driver removeProfilePhoto(String username) throws IOException {
        // Find the driver
        Driver driver = driverRepository.findByUsername(username);
        
        // Throw exception if no driver is found
        if (driver == null) {
            throw new IllegalArgumentException("Driver not found...");
        }

        // Get the photo URL
        String photoUrl = driver.getProfilePhotoUrl();

        // Check if a photo URL exists
        if (photoUrl != null && !photoUrl.isEmpty()) {
            // Extract the filename from the URL (e.g., from "/api/files/image.jpg")
            String fileName = photoUrl.substring(photoUrl.lastIndexOf('/') + 1);
            
            // Delete the physical file
            fileUploadService.deleteFile(fileName);
            
            // Clear the URL from the driver's record
            driver.setProfilePhotoUrl(null);
            
            // Save the updated driver
            return driverRepository.save(driver);
        }
        
        // If no photo existed, just return the driver as is
        return driver;
    }
}