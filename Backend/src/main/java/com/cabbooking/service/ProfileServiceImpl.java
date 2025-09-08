package com.cabbooking.service;

import com.cabbooking.dto.UserProfileUpdateRequest;
import com.cabbooking.model.AbstractUser;
import com.cabbooking.repository.AdminRepository;
import com.cabbooking.repository.CustomerRepository;
import com.cabbooking.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cabbooking.model.Admin;
import com.cabbooking.model.Customer;
import com.cabbooking.model.Driver;

import java.util.Optional;

/**
 * Implementation of the IProfileService for updating user profiles.
 * 
 * This service handles the business logic for updating user profiles,
 * including partial updates and retrieving user profiles by username.
 * 
 * Main Responsibilities:
 * - Provides the business logic for updating user profiles.
 * - Retrieves user profiles by username.
 * 
 * Security:
 * - All endpoints are secured using method-level security.
 * - Only users with the 'Driver' or 'Admin' role can access these endpoints.
 * - Users can only update their own profile.
 * - Admins can update any user's profile.
 * - Drivers can only update their own profile.
 */
@Service
public class ProfileServiceImpl implements IProfileService {

    /*
     * Repository for Admin entity.
     * Provides access to the Admin table in the database.
     */
    @Autowired private AdminRepository adminRepository;

    /*
     * Repository for Customer entity.
     * Provides access to the Customer table in the database.
     */
    @Autowired private CustomerRepository customerRepository;

    /*
     * Repository for Driver entity.
     * Provides access to the Driver table in the database.
     */
    @Autowired private DriverRepository driverRepository;

    /**
     * Finds a user by their unique username across all user type repositories.
     *
     * Workflow:
     * - Search in Admin repository first.
     * - If not an admin, search in Customer repository.
     * - Finally, search in Driver repository.
     * - If no user is found, return an empty Optional.
     * 
     * @param username The username to search for.
     * @return An Optional containing the AbstractUser if found.
     */
    @Override
    public Optional<AbstractUser> getUserProfileByUsername(String username) {
        // Search in Admin repository first
        Admin admin = adminRepository.findByUsername(username);
        if (admin != null) {
            return Optional.of(admin);
        }

        // If not an admin, search in Customer repository
        Customer customer = customerRepository.findByUsername(username);
        if (customer != null) {
            return Optional.of(customer);
        }

        // Finally, search in Driver repository
        Driver driver = driverRepository.findByUsername(username);
        if (driver != null) {
            return Optional.of(driver);
        }

        // If no user is found, return an empty Optional
        return Optional.empty();
    }

    /*
     * Updates a user's profile.
     * 
     * Workflow:
     * - Checks if the user is authenticated.
     * - Calls the service layer to update the user's profile.
     * - Returns a ResponseEntity containing the updated user profile.
     * 
     * @param username The username of the user whose profile is to be updated.
     * @param request The DTO with the fields to update.
     * @return A ResponseEntity containing the updated user profile.
     */
    @Override
    @Transactional
    public AbstractUser updateUserProfile(String username, UserProfileUpdateRequest request) {
        AbstractUser user = getUserProfileByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User with username '" + username + "' not found."));

        // Partially update the user's details
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        if (request.getMobileNumber() != null) {
            user.setMobileNumber(request.getMobileNumber());
        }

        // Check if the user is a Driver and if a license number was provided
        if (user instanceof Driver && request.getLicenceNo() != null) {
            ((Driver) user).setLicenceNo(request.getLicenceNo());
        }

        return saveUser(user);
    }

    /*
     * Checks if a username is already taken across all user types.
     * 
     * Workflow:
     * - Checks each user type repository for the existence of the username.
     * - Returns true if the username exists in any repository, false otherwise.
     * 
     * @param username The username to check.
     * @return True if the username exists, false otherwise.
     */
    @Override
    public boolean isUsernameTaken(String username) {
        return adminRepository.existsByUsername(username) ||
               customerRepository.existsByUsername(username) ||
               driverRepository.existsByUsername(username);
    }

    /* ==============
     * HELPER METHODS
     * ==============
     */

    /*
     * Helper method to save a user regardless of their type.
     */
    private AbstractUser saveUser(AbstractUser user) {
        if (user instanceof Admin) {
            return adminRepository.save((Admin) user);
        } else if (user instanceof Customer) {
            return customerRepository.save((Customer) user);
        } else if (user instanceof Driver) {
            return driverRepository.save((Driver) user);
        }
        throw new IllegalStateException("Unknown user type cannot be saved.");
    }
}