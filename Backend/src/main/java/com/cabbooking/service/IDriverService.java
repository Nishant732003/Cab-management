package com.cabbooking.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.cabbooking.model.Driver;


/**
 * Service interface for driver management operations.
 */
public interface IDriverService {

    /**
     * Retrieves a list of the best-performing drivers based on their rating.
     *
     * @return A list of Driver entities considered to be "best drivers" (e.g., rating >= 4.5).
     */
    List<Driver> getBestDrivers();

    /**
     * Retrieves a list of all drivers who have registered but are not yet verified.
     *
     * @return A list of unverified Driver entities.
     */
    List<Driver> getUnverifiedDrivers();

    /**
     * Verifies a driver's account, allowing them to be assigned to trips.
     *
     * @param driverId The unique ID of the driver to be verified.
     * @return A message of successful verification.
     * @throws IllegalArgumentException if admin does not exist
     */
    String verifyDriver(int driverId);

    /**
     * Uploads a profile photo for a driver and updates their record.
     * @param username The username of the driver.
     * @param file The image file to upload.
     * @return The updated Driver object with the new photo URL.
     * @throws IOException if the file upload fails.
     */
    Driver uploadProfilePhoto(String username, MultipartFile file) throws IOException;

    /**
     * Removes the profile photo for a given driver.
     * This involves deleting the file from storage and clearing the URL in the database.
     * @param username The username of the driver.
     * @return The updated Driver object with the photo URL removed.
     * @throws IOException if the file deletion fails.
     */
    Driver removeProfilePhoto(String username) throws IOException;
}