package com.cabbooking.controller;

import java.io.IOException;
import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cabbooking.model.Driver;
import com.cabbooking.repository.DriverRepository;
import com.cabbooking.service.IDriverService;

/**
 * REST controller for driver-specific operations.
 * 
 * These are actions that an authenticated driver can perform on their own account.
 * 
 * Main Responsibilities:
 * - Upload and remove driver profile photos.
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

    // Service to inject the driver service
    @Autowired
    private IDriverService driverService;

    // Service to inject the driver repository
    @Autowired
    private DriverRepository driverRepository;

    /**
     * Endpoint for a driver to upload their profile photo.
     * 
     * POST /api/drivers/upload-photo
     * 
     * Workflow: 
     * - User sends driverId, profile photo file and Principal (injected by Spring Security).
     * - A driver is fetched based on the logged-in user's username.
     * - If the driver is not found or the logged-in driver does not match the driverId, return 403 Forbidden.
     * - Delegate the file upload and database update to the driver service layer.
     * - Return the updated driver object with the new photo URL.
     * - Return any error that occurs during the process.
     *
     * @param file The image file sent as multipart/form-data.
     * @param principal The currently authenticated user, injected by Spring
     * Security.
     * @return The updated Driver object with the new photo URL.
     */
    @PostMapping("/upload-photo")
    public ResponseEntity<Driver> uploadProfilePhoto(@RequestParam("file") MultipartFile file,
            Principal principal) {

        logger.info("Received upload-photo request for driver username: {}", principal.getName());

        // Find the driver record based on the logged-in user's username
        Driver loggedInDriver = driverRepository.findByUsername(principal.getName());

        // Ensure the logged-in driver can only update their own profile
        if (loggedInDriver == null) {
            logger.warn("Unauthorized access attempt for driver username: {}", principal.getName());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Return 403 Forbidden
        }

        try {
            logger.info("Uploading profile photo for driver username: {}", principal.getName());
            // Delegate the file upload and database update to the service layer
            Driver updatedDriver = driverService.uploadProfilePhoto(principal.getName(), file);
            return ResponseEntity.ok(updatedDriver);
        } catch (IOException e) {
            logger.error("Error uploading profile photo for driver username: {}", principal.getName(), e);
            // Handle potential file system errors
            return ResponseEntity.internalServerError().build();
        } catch (IllegalArgumentException e) {
            logger.error("Invalid driver username: {}", principal.getName(), e);
            // Handle cases where the driverId is not found
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint for a driver to remove their own profile photo.
     * 
     * DELETE /api/drivers/delete-photo
     * 
     * Workflow: 
     * - Checks if the user is authenticated and has the 'Driver' role. 
     * - Finds the driver record based on the logged-in user's username. 
     * - Ensures the logged-in driver can only update their own profile. 
     * - Delegates the file deletion and database update to the service layer. 
     * - Returns the updated Driver object with the new photo URL.
     *
     * @param principal The currently authenticated user.
     * @return A response entity with the updated driver or an error status.
     */
    @DeleteMapping("/delete-photo")
    public ResponseEntity<Driver> removeProfilePhoto(Principal principal) {

        logger.info("Received remove-photo request for driver username: {}", principal.getName());

        Driver loggedInDriver = driverRepository.findByUsername(principal.getName());
        if (loggedInDriver == null) {
            logger.warn("Unauthorized access attempt for driver username: {}", principal.getName());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            logger.info("Removing profile photo for driver username: {}", principal.getName());
            Driver updatedDriver = driverService.removeProfilePhoto(principal.getName());
            return ResponseEntity.ok(updatedDriver);
        } catch (IOException e) {
            logger.error("Error removing profile photo for driver username: {}", principal.getName(), e);
            return ResponseEntity.internalServerError().build();
        } catch (IllegalArgumentException e) {
            logger.error("Invalid driver username: {}", principal.getName(), e);
            return ResponseEntity.notFound().build();
        }
    }
}
