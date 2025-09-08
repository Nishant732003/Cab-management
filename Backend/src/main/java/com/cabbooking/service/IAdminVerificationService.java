package com.cabbooking.service;

import java.util.List;

import com.cabbooking.model.Admin;

/**
 * Service interface for superadmin actions related to verifying admin accounts.
 */
public interface IAdminVerificationService {

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
     * @return A message of successful verification
     * @throws IllegalArgumentException if admin does not exist
     */
    String verifyAdmin(Integer adminId);
}
