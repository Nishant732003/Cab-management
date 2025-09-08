package com.cabbooking.service;


import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cabbooking.model.Admin;
import com.cabbooking.model.Customer;
import com.cabbooking.model.Driver;
import com.cabbooking.repository.AdminRepository;
import com.cabbooking.repository.CustomerRepository;
import com.cabbooking.repository.DriverRepository;

/**
 * Implementation of the IUserDeletionService.
 * Handles the logic for deleting any type of user from the system.
 * 
 * Main Responsibilities:
 * - Deletes a user from the system, regardless of their type (Admin, Customer, or Driver).
 * 
 * Security:
 * - All endpoints are secured using method-level security.
 */
@Service
public class UserDeletionServiceImpl implements IUserDeletionService {

    /*
     * Repository for Admin entity.
     * Provides access to the Admin table in the database.
     */
    @Autowired
    private AdminRepository adminRepository;

    /*
     * Repository for Customer entity.
     * Provides access to the Customer table in the database.
     */
    @Autowired
    private CustomerRepository customerRepository;

    /*
     * Repository for Driver entity.
     * Provides access to the Driver table in the database.
     */
    @Autowired
    private DriverRepository driverRepository;

    /*
     * Service for handling file uploads.
     * Provides methods for uploading and deleting files.
     */
    @Autowired
    private IFileUploadService fileUploadService;

    /**
     * Deletes a user by their username.
     *
     * Workflow:
     * - It attempts to delete from the admin repository first.
     * - If not found, it tries the customer repository.
     * - If still not found, it tries the driver repository.
     * - If the user is not found in any repository, it throws an exception.
     *
     * @param userId The username of the user to delete.
     * @throws IOException If there's an error deleting the driver.
     */
    @Override
    @Transactional
    public void deleteUser(String username) throws IOException {
        // Try to find and delete as an Admin
        Admin admin = adminRepository.findByUsername(username);
        if (admin != null) {
            adminRepository.delete(admin);
            return;
        }

        // If not an Admin, try as a Customer
        Customer customer = customerRepository.findByUsername(username);
        if (customer != null) {
            customerRepository.delete(customer);
            return;
        }

        // If not a Customer, try as a Driver
        Driver driver = driverRepository.findByUsername(username);
        if (driver != null) {
            // Check if the driver has a profile photo
            if (driver.getProfilePhotoUrl() != null && !driver.getProfilePhotoUrl().isEmpty()) {
                try {
                    // Extract the filename from the URL
                    String fileName = driver.getProfilePhotoUrl().substring(driver.getProfilePhotoUrl().lastIndexOf('/') + 1);
                    // Delete the physical file
                    fileUploadService.deleteFile(fileName);
                } catch (IOException e) {
                    // If there's an error deleting the file, throw an exception
                    throw new IOException("Error deleting driver profile photo: " + e.getMessage());
                }
            }
            // Finally, delete the driver record
            driverRepository.delete(driver);
            return;
        }

        // If user was not found in any repository
        throw new IllegalArgumentException("User with username " + username + " not found.");
    }
}