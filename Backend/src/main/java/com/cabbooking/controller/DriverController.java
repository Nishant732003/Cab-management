package com.cabbooking.controller;

import com.cabbooking.model.Driver;
import com.cabbooking.repository.DriverRepository;
import com.cabbooking.service.IDriverService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

/**
 * REST controller for driver-specific operations. These are actions that an
 * authenticated driver can perform on their own account. 
 * Main Responsibilities:
 * - Provides endpoints for uploading and removing profile photos. 
 * 
 * Security: 
 * - All endpoints are secured and require the user to have the 'Driver' role. 
 * - Only drivers can upload and remove their own profile photos.
 */
@RestController
@RequestMapping("/api/drivers")
@PreAuthorize("hasRole('Driver')")
public class DriverController {

    // SLF4J Logger for tracking requests and actions in this controller
    private static final Logger logger = LoggerFactory.getLogger(DriverController.class);

    // Serice to inject the driver service
    @Autowired
    private IDriverService driverService;

    // Service to inject the driver repository
    @Autowired
    private DriverRepository driverRepository;

    /**
     * Endpoint for a driver to upload their profile photo. 
     * POST /api/drivers/{driverId}/upload-profile-photo 
     * Workflow: 
     * - Checks if the user is authenticated and has the 'Driver' role. 
     * - Finds the driver record based on the logged-in user's username. 
     * - Ensures the logged-in driver can only update their own profile. 
     * - Delegates the file upload and database update to the service layer. 
     * - Returns the updated Driver object with the new photo URL.
     *
     * @param driverId The ID of the driver.
     * @param file The image file sent as multipart/form-data.
     * @param principal The currently authenticated user, injected by Spring
     * Security.
     * @return The updated Driver object with the new photo URL.
     */
    @PostMapping("/{driverId}/upload-profile-photo")
    public ResponseEntity<Driver> uploadProfilePhoto(@PathVariable int driverId,
            @RequestParam("file") MultipartFile file,
            Principal principal) {

        logger.info("Received upload-profile-photo request for driverId: {}", driverId);

        // Find the driver record based on the logged-in user's username
        Driver loggedInDriver = driverRepository.findByUsername(principal.getName());

        // Ensure the logged-in driver can only update their own profile
        if (loggedInDriver == null || loggedInDriver.getId() != driverId) {
            logger.warn("Unauthorized access attempt for driverId: {}", driverId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Return 403 Forbidden
        }

        try {
            logger.info("Uploading profile photo for driverId: {}", driverId);
            // Delegate the file upload and database update to the service layer
            Driver updatedDriver = driverService.uploadProfilePhoto(driverId, file);
            return ResponseEntity.ok(updatedDriver);
        } catch (IOException e) {
            logger.error("Error uploading profile photo for driverId: {}", driverId, e);
            // Handle potential file system errors
            return ResponseEntity.internalServerError().build();
        } catch (IllegalArgumentException e) {
            logger.error("Invalid driverId: {}", driverId, e);
            // Handle cases where the driverId is not found
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint for a driver to remove their own profile photo. 
     * DELETE /api/drivers/{driverId}/delete-profile-photo 
     * Workflow: 
     * - Checks if the user is authenticated and has the 'Driver' role. 
     * - Finds the driver record based on the logged-in user's username. 
     * - Ensures the logged-in driver can only update their own profile. 
     * - Delegates the file deletion and database update to the service layer. 
     * - Returns the updated Driver object with the new photo URL.
     *
     * @param driverId The ID of the driver whose photo is to be removed.
     * @param principal The currently authenticated user.
     * @return A response entity with the updated driver or an error status.
     */
    @DeleteMapping("/{driverId}/delete-profile-photo")
    public ResponseEntity<Driver> removeProfilePhoto(@PathVariable int driverId, Principal principal) {

        logger.info("Received remove-profile-photo request for driverId: {}", driverId);

        Driver loggedInDriver = driverRepository.findByUsername(principal.getName());
        if (loggedInDriver == null || loggedInDriver.getId() != driverId) {
            logger.warn("Unauthorized access attempt for driverId: {}", driverId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            logger.info("Removing profile photo for driverId: {}", driverId);
            Driver updatedDriver = driverService.removeProfilePhoto(driverId);
            return ResponseEntity.ok(updatedDriver);
        } catch (IOException e) {
            logger.error("Error removing profile photo for driverId: {}", driverId, e);
            return ResponseEntity.internalServerError().build();
        } catch (IllegalArgumentException e) {
            logger.error("Invalid driverId: {}", driverId, e);
            return ResponseEntity.notFound().build();
        }
    }
}
