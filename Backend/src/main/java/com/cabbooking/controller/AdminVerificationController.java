package com.cabbooking.controller;

import com.cabbooking.model.Admin;
import com.cabbooking.service.AdminVerificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AdminVerificationController is a REST controller used by the superadmin to manage 
 * the verification of admin accounts in the Cab Booking Platform.
 * 
 * Main Responsibilities:
 * - Allows the superadmin to view all unverified admin accounts.
 * - Allows the superadmin to verify (activate) an admin by their ID.
 *
 * Endpoints exposed:
 * - GET  /api/admins/unverified         : Returns a list of all unverified admins.
 * - POST /api/admins/{adminId}/verify   : Verifies (activates) an admin account.
 *
 * Security Note:
 * - **IMPORTANT**: Only the superadmin with username "harshit" should have access to these endpoints.
 *   This access control must be enforced in Spring Security configuration or with method-level security
 *   (not handled in this controller class directly for separation of concerns).
 * - In your security config, use either URL-based, role-based, or username-based restrictions for these endpoints.
 */
@RestController
@RequestMapping("/api/admins")
public class AdminVerificationController {

    // SLF4J Logger for tracking requests and actions in this controller
    private static final Logger logger = LoggerFactory.getLogger(AdminVerificationController.class);

    // Service responsible for the business logic of retrieving and verifying admin accounts
    @Autowired
    private AdminVerificationService verificationService;

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
    @GetMapping("/unverified")
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
     * - All verification business logic is handled by the AdminVerificationService.
     * - Handles and logs validation errors (e.g., if ID is not found).
     *
     * Example usage: Called when the superadmin approves an admin through the UI.
     *
     * @param adminId Unique ID of the admin to be verified
     * @return HTTP 200 with success message on success, or HTTP 400/500 with error message on failure.
     */
    @PostMapping("/{adminId}/verify")
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
}
