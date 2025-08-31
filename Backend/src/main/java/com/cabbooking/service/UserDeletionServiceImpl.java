package com.cabbooking.service;


import com.cabbooking.model.Customer;
import com.cabbooking.model.Driver;
import com.cabbooking.model.Admin;
import com.cabbooking.repository.AdminRepository;
import com.cabbooking.repository.CustomerRepository;
import com.cabbooking.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

/**
 * Implementation of the IUserDeletionService.
 * Handles the logic for deleting any type of user from the system.
 */
@Service
public class UserDeletionServiceImpl implements IUserDeletionService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DriverRepository driverRepository;

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
     */
    @Override
    @Transactional
    public void deleteUser(String username) {
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
            // ==> THIS IS THE NEW LOGIC <==
            // Check if the driver has a profile photo
            if (driver.getProfilePhotoUrl() != null && !driver.getProfilePhotoUrl().isEmpty()) {
                try {
                    // Extract the filename from the URL
                    String fileName = driver.getProfilePhotoUrl().substring(driver.getProfilePhotoUrl().lastIndexOf('/') + 1);
                    // Delete the physical file
                    fileUploadService.deleteFile(fileName);
                } catch (IOException e) {
                    // In a production app, you would log this error more robustly
                    System.err.println("Error deleting profile photo for driver " + username + ": " + e.getMessage());
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