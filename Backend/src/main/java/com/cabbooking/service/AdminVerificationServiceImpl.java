package com.cabbooking.service;

import com.cabbooking.model.Admin;
import com.cabbooking.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service implementation class for the AdminVerificationService interface.
 *
 * Responsible for managing verification status of Admin accounts,
 * specifically:
 * - Retrieving all admins who are not yet verified.
 * - Verifying (activating) an admin by setting their 'verified' status to true.
 *
 * This service interacts directly with the AdminRepository to perform database operations.
 */
@Service
public class AdminVerificationServiceImpl implements AdminVerificationService {

    /**
     * Repository used for CRUD operations on Admin entities.
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
     * NOTE:
     * - The filtering is done in-memory after retrieving all admins. For better performance with a large user base,
     *   consider adding a custom query method in AdminRepository like:
     *   `List<Admin> findByVerifiedFalse();`
     *
     * @return List of unverified Admin users.
     */
    @Override
    public List<Admin> getUnverifiedAdmins() {
        List<Admin> allAdmins = adminRepository.findAll();
        return allAdmins.stream()
                .filter(admin -> admin.getVerified() != null && !admin.getVerified())
                .collect(Collectors.toList());
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
     * - Called when superadmin approves an admin registration after validation.
     *
     * @param adminId The unique identifier of the Admin to verify.
     * @return The updated Admin entity after setting verified=true.
     * @throws IllegalArgumentException if no Admin with the given ID is found.
     */
    @Override
    public Admin verifyAdmin(Integer adminId) {
        Optional<Admin> adminOpt = adminRepository.findById(adminId);

        if (!adminOpt.isPresent()) {
            throw new IllegalArgumentException("Admin with id " + adminId + " not found");
        }

        Admin admin = adminOpt.get();
        admin.setVerified(true);
        return adminRepository.save(admin);
    }
}
