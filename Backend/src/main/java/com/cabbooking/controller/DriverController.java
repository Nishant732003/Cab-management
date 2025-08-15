package com.cabbooking.controller;

import com.cabbooking.model.Driver;
import com.cabbooking.repository.DriverRepository;
import com.cabbooking.service.IDriverService;
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
 */
@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    @Autowired
    private IDriverService driverService;

    @Autowired
    private DriverRepository driverRepository; // Used for the security check

    /**
     * Endpoint for a driver to upload their profile photo.
     *
     * @param driverId The ID of the driver.
     * @param file The image file sent as multipart/form-data.
     * @param principal The currently authenticated user, injected by Spring
     * Security.
     * @return The updated Driver object with the new photo URL.
     */
    @PostMapping("/{driverId}/upload-profile-photo")
    @PreAuthorize("hasRole('Driver')")
    public ResponseEntity<Driver> uploadProfilePhoto(@PathVariable int driverId,
            @RequestParam("file") MultipartFile file,
            Principal principal) {

        // --- Security Check ---
        // Find the driver record based on the logged-in user's username
        Driver loggedInDriver = driverRepository.findByUsername(principal.getName());

        // Ensure the logged-in driver can only update their own profile
        if (loggedInDriver == null || loggedInDriver.getId() != driverId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Return 403 Forbidden
        }

        try {
            // Delegate the file upload and database update to the service layer
            Driver updatedDriver = driverService.uploadProfilePhoto(driverId, file);
            return ResponseEntity.ok(updatedDriver);
        } catch (IOException e) {
            // Handle potential file system errors
            return ResponseEntity.internalServerError().build();
        } catch (IllegalArgumentException e) {
            // Handle cases where the driverId is not found
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint for a driver to remove their own profile photo.
     *
     * @param driverId The ID of the driver whose photo is to be removed.
     * @param principal The currently authenticated user.
     * @return A response entity with the updated driver or an error status.
     */
    @DeleteMapping("/{driverId}/delete-profile-photo")
    @PreAuthorize("hasRole('Driver')")
    public ResponseEntity<Driver> removeProfilePhoto(@PathVariable int driverId, Principal principal) {

        // --- Security Check ---
        Driver loggedInDriver = driverRepository.findByUsername(principal.getName());
        if (loggedInDriver == null || loggedInDriver.getId() != driverId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            Driver updatedDriver = driverService.removeProfilePhoto(driverId);
            return ResponseEntity.ok(updatedDriver);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
