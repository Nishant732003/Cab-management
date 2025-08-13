package com.cabbooking.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cabbooking.model.Admin;
import com.cabbooking.model.Driver;
import com.cabbooking.service.IAdminVerificationService;
import com.cabbooking.service.IDriverService;

/**
 * REST controller for handling admin-specific operations related to driver management.
 * * Main Responsibilities:
 * - Provides endpoints for viewing and verifying driver accounts.
 * * Security:
 * - All endpoints within this controller are secured using method-level security.
 * - Only users with the 'Admin' role are authorized to access these functionalities.
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('Admin')") // Secures all endpoints in this controller
@Validated
public class AdminController {

    // SLF4J Logger for tracking requests and actions in this controller
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    // Service responsible for the business logic of retrieving and verifying admin accounts
    @Autowired
    private IAdminVerificationService verificationService;

    // Service layer injected to handle driver-related business logic.
    @Autowired
    private IDriverService driverService;

    /**
     * Endpoint to retrieve all unverified admin accounts.
     * 
     * GET /api/admins/unverified
     * 
     * Workflow:
     * - Used by the superadmin to see which admins have registered but are pending verification.
     * - Calls the service layer to fetch all admins with verified == false.
     * - Returns the list as JSON.
     *
     * Example usage: For UI listing of all accounts waiting for approval.
     *
     * @return HTTP 200 with List<Admin> containing all unverified admins in the system.
     */
    @GetMapping("/unverified/admins")
    public ResponseEntity<List<Admin>> getUnverifiedAdmins() {
        logger.info("Superadmin requested list of unverified admins");
        List<Admin> unverifiedAdmins = verificationService.getUnverifiedAdmins();
        return ResponseEntity.ok(unverifiedAdmins);
    }

    /**
     * Endpoint to verify an admin account by its unique ID.
     * 
     * POST /api/admins/{adminId}/verify
     * 
     * Workflow:
     * - Used by the superadmin after reviewing registration details of an admin.
     * - Marks the specific admin account as verified (activated), allowing them to access admin functionalities.
     * - All verification business logic is handled by the IAdminVerificationService.
     * - Handles and logs validation errors (e.g., if ID is not found).
     *
     * Example usage: Called when the superadmin approves an admin through the UI.
     *
     * @param adminId Unique ID of the admin to be verified
     * @return HTTP 200 with success message on success, or HTTP 400/500 with error message on failure.
     */
    @PostMapping("/verify/admins/{adminId}")
    public ResponseEntity<String> verifyAdmin(@PathVariable Integer adminId) {
        logger.info("Superadmin trying to verify admin with id: {}", adminId);
        try {
            verificationService.verifyAdmin(adminId);
            return ResponseEntity.ok("Admin verified successfully");
        } catch (IllegalArgumentException e) {
            // Handles known issues like "admin not found"
            logger.warn("Admin verification failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Handles any unexpected server or database error
            logger.error("Unexpected error verifying admin", e);
            return ResponseEntity.internalServerError().body("An error occurred");
        }
    }

    /**
     * Endpoint to retrieve a list of all drivers awaiting verification.
     * * GET /api/admin/drivers/unverified
     * * Workflow:
     * - An admin calls this endpoint to get a list of pending driver registrations.
     * - Delegates to the driverService to fetch all drivers with verified = false.
     * - Returns the list of drivers as a JSON array.
     * * @return ResponseEntity containing a list of unverified Driver objects.
     */
    @GetMapping("/unverified/drivers")
    public ResponseEntity<List<Driver>> getUnverifiedDrivers() {
        List<Driver> unverifiedDrivers = driverService.viewUnverifiedDrivers();
        return ResponseEntity.ok(unverifiedDrivers);
    }

    /**
     * Endpoint to verify a specific driver by their ID.
     * * POST /api/admin/drivers/{driverId}/verify
     * * Workflow:
     * - An admin calls this endpoint to approve a driver's registration.
     * - The driver's 'verified' status is set to true in the database.
     * - The updated Driver object is returned.
     * * @param driverId The unique ID of the driver to be verified.
     * @return ResponseEntity containing the updated and now-verified Driver object.
     */
    @PostMapping("/verify/drivers/{driverId}")
    public ResponseEntity<Driver> verifyDriver(@PathVariable int driverId) {
        Driver verifiedDriver = driverService.verifyDriver(driverId);
        return ResponseEntity.ok(verifiedDriver);
    }
}