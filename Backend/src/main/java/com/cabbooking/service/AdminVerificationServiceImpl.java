package com.cabbooking.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cabbooking.model.Admin;
import com.cabbooking.repository.AdminRepository;

/**
 * Service implementation class for the IAdminVerificationService interface.
 *
 * Responsible for managing verification status of Admin accounts,
 * specifically:
 * - Retrieving all admins who are not yet verified.
 * - Verifying (activating) an admin by setting their 'verified' status to true.
 *
 * This service interacts directly with the AdminRepository to perform database operations.
 * 
 * Main Responsibilities:
 * - Retrieves a list of unverified Admin accounts.
 * - Verifies an Admin account by adminId.
 * 
 * Dependencies:
 * - AdminRepository for accessing admin data in the database.
 */
@Service
public class AdminVerificationServiceImpl implements IAdminVerificationService {

    /**
     * Repository used for CRUD operations on Admin entities.
     * Provides access to the underlying database.
     */
    @Autowired
    private AdminRepository adminRepository;

    /**
     * Retrieves a list of all Admin users whose accounts are currently unverified.
     *
     * Workflow:
     * - Fetches all Admin entities from the database.
     * - Filters the list to only include Admins with 'verified' field set to false (or non-null and false).
     * - Returns the filtered list for further processing or display.
     *
     * Use case:
     * - This method is used by superadmin to view pending admin registration requests for verification.
     *
     * @return List of unverified Admin users.
     */
    @Override
    public List<Admin> getUnverifiedAdmins() {
        return adminRepository.findByVerifiedFalse();
    }

    /**
     * Verifies an Admin account by adminId, marking it as activated.
     *
     * Workflow:
     * - Attempts to find an Admin entity by the provided unique identifier.
     * - If the admin does not exist, throws IllegalArgumentException.
     * - If found, sets the 'verified' flag on the Admin entity to true, indicating approval.
     * - Saves the updated Admin entity back to the database.
     *
     * Use case:
     * - Called when superadmin verifies an admin after registration.
     *
     * @param adminId The unique identifier of the Admin to verify.
     * @return A message indicating successful verification.
     * @throws IllegalArgumentException if no Admin with the given ID is found.
     */
    @Override
    public String verifyAdmin(Integer adminId) {
        Admin admin = adminRepository.findById(adminId).orElseThrow(() -> new IllegalArgumentException("Driver not found with id: " + adminId));
        admin.setVerified(true);
        adminRepository.save(admin);
        return "Admin verified successfully";
    }
}
