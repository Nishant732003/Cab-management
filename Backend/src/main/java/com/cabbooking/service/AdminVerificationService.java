package com.cabbooking.service;

import com.cabbooking.model.Admin;

import java.util.List;

/**
 * Service interface for superadmin actions related to verifying admin accounts.
 */
public interface AdminVerificationService {

    /**
     * Lists all admins that are not yet verified (verified == false)
     *
     * @return list of unverified admins
     */
    List<Admin> getUnverifiedAdmins();

    /**
     * Verifies an admin account by setting verified = true.
     *
     * @param adminId ID of the admin to verify
     * @return the updated Admin entity
     * @throws IllegalArgumentException if admin does not exist
     */
    Admin verifyAdmin(Integer adminId);
}
