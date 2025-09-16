package com.cabbooking.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cabbooking.dto.UserSummaryDTO;
import com.cabbooking.model.Admin;
import com.cabbooking.model.Driver;
import com.cabbooking.service.IAdminService;
import com.cabbooking.service.IAdminVerificationService;
import com.cabbooking.service.IDriverService;

/**
 * REST controller for handling admin-specific operations.
 * 
 * Endpoints:
 * - GET /api/admin/unverified/admins: Retrieve all unverified admin accounts.
 * - POST /api/admin/verify/admins/{adminId}: Verify an admin account by ID.
 * - GET /api/admin/unverified/drivers: Retrieve all unverified driver accounts.
 * - POST /api/admin/verify/drivers/{driverId}: Verify a driver account by ID.
 * - GET /api/admin/customers: Retrieve a summary list of all customers.
 * - GET /api/admin/drivers: Retrieve a summary list of all drivers.
 * 
 * Main Responsibilities:
 * - Manage verification of admin and driver accounts.
 * - Provide summary lists of customers and drivers.
 * - Access trip histories based on driver or date.
 * 
 * Dependencies:
 * - IAdminVerificationService: Service for admin verification logic.
 * - IDriverService: Service for driver-related operations.
 * - IAdminService: Service for admin-related operations.
 * - ITripBookingService: Service for trip booking-related operations.
 */
@RestController
@RequestMapping("/api/admin")
@Validated
public class AdminController {

    // SLF4J Logger for tracking requests and actions in this controller
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    // Service layer injected to handle admin verification logic
    @Autowired
    private IAdminVerificationService verificationService;

    // Service layer injected to handle driver-related business logic
    @Autowired
    private IDriverService driverService;

    // Service layer injected to handle admin-related business logic
    @Autowired
    private IAdminService adminService;

    /**
     * Endpoint to retrieve all unverified admin accounts.
     * 
     * GET /api/admin/unverified/admins
     *  
     * Workflow: 
     * - Used by the superadmin to see which admins have registered but are pending verification. 
     * - Calls the service layer to fetch all admins with verified == false. 
     * - Returns the list as JSON.
     *
     * @return HTTP 200 with List<Admin> containing all unverified admins in the
     * system.
     */
    @GetMapping("/unverified/admins")
    public ResponseEntity<List<Admin>> getUnverifiedAdmins() {
        logger.info("Superadmin requested list of unverified admins");
        List<Admin> unverifiedAdmins = verificationService.getUnverifiedAdmins();
        logger.info("Found {} unverified admins", unverifiedAdmins.size());
        return ResponseEntity.ok(unverifiedAdmins);
    }

    /**
     * Endpoint to verify an admin account by its unique ID.
     * 
     * POST /api/admin/verify/admins/{adminId}
     *  
     * Workflow: 
     * - Used by the superadmin after reviewing registration details of an admin. 
     * - Marks the specific admin account as verified (activated), allowing them to access admin functionalities.  
     * - Handles and logs validation errors (e.g., if the ID is not found).
     *
     * @param adminId Unique ID of the admin to be verified
     * @return HTTP 200 with success message on success, or HTTP 400/500 with
     * error message on failure.
     */
    @PostMapping("/verify/admins/{adminId}")
    public ResponseEntity<String> verifyAdmin(@PathVariable int adminId) {
        logger.info("Superadmin trying to verify admin with id: {}", adminId);
        try {
            String message = verificationService.verifyAdmin(adminId);
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException e) {
            // Handles known issues like "admin not found"
            logger.warn("Admin verification failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Handles any unexpected server or database error
            logger.error("Unexpected error verifying admin", e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    /**
     * Endpoint to retrieve a list of all drivers awaiting verification.
     * 
     * GET /api/admin/unverified/drivers
     * 
     * Workflow: 
     * - An admin calls this endpoint to get a list of drivers pending verification. 
     * - Delegates to the driverService to fetch all drivers with verified = false.
     * - Returns the list of drivers as a JSON array.
     *
     * @return ResponseEntity containing a list of unverified Driver objects.
     */
    @GetMapping("/unverified/drivers")
    public ResponseEntity<List<Driver>> getUnverifiedDrivers() {
        logger.info("Admin requested list of unverified drivers");
        List<Driver> unverifiedDrivers = driverService.getUnverifiedDrivers();
        logger.info("Found {} unverified drivers", unverifiedDrivers.size());
        return ResponseEntity.ok(unverifiedDrivers);
    }

    /**
     * Endpoint to verify a specific driver by their ID.
     * 
     * POST /api/admin/verify/drivers/{driverId}
     * 
     * Workflow: 
     * - An admin calls this endpoint to verify a driver. 
     * - The driver's 'verified' status is set to true in the database. 
     * - The updated Driver object is returned.
     *
     * @param driverId The unique ID of the driver to be verified.
     * @return ResponseEntity containing the updated and now-verified Driver
     * object.
     */
  @PostMapping("/verify/drivers/{driverId}")
public ResponseEntity<Map<String, String>> verifyDriver(@PathVariable int driverId) {
    logger.info("Admin trying to verify driver with id: {}", driverId);
    try {
        String message = driverService.verifyDriver(driverId);
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
        logger.warn("Driver verification failed: {}", e.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("error", e.getMessage());
        return ResponseEntity.badRequest().body(response);
    } catch (Exception e) {
        logger.error("Unexpected error verifying driver", e);
        Map<String, String> response = new HashMap<>();
        response.put("error", e.getMessage());
        return ResponseEntity.internalServerError().body(response);
    }
}


    /**
     * Endpoint for an admin to retrieve a summary list of all customers. 
     * 
     * GET /api/admin/customers 
     * 
     * Workflow:
     * - An admin calls this endpoint to get a list of all registered customers. 
     * - Calls the adminService to fetch all customers from the database. 
     * - Maps each Customer to a UserSummaryDTO to include only essential information. 
     * - Returns the list of UserSummaryDTOs as a JSON array.
     *
     * @return A ResponseEntity containing the list of all customers' summaries.
     */
    @GetMapping("/customers")
    public ResponseEntity<List<UserSummaryDTO>> getAllCustomers() {
        logger.info("Admin requested a list of all customers.");
        List<UserSummaryDTO> customers = adminService.getAllCustomers();
        logger.info("Found {} customers", customers.size());
        return ResponseEntity.ok(customers);
    }

    /**
     * Endpoint for an admin to retrieve a summary list of all drivers. 
     * 
     * GET /api/admin/drivers 
     * 
     * Workflow: 
     * - An admin calls this endpoint to get a list of all registered drivers. 
     * - Calls the adminService to fetch all drivers from the database. 
     * - Maps each Driver to a UserSummaryDTO to include only essential information. 
     * - Returns the list of UserSummaryDTOs as a JSON array.
     *
     * @return A ResponseEntity containing the list of all drivers' summaries.
     */
    @GetMapping("/drivers")
    public ResponseEntity<List<UserSummaryDTO>> getAllDrivers() {
        logger.info("Admin requested a list of all drivers.");
        List<UserSummaryDTO> drivers = adminService.getAllDrivers();
        logger.info("Found {} drivers", drivers.size());
        return ResponseEntity.ok(drivers);
    }
}
