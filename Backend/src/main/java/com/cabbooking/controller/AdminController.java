package com.cabbooking.controller;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cabbooking.dto.UserSummaryDTO;
import com.cabbooking.model.Admin;
import com.cabbooking.model.Driver;
import com.cabbooking.model.TripBooking;
import com.cabbooking.service.IAdminService;
import com.cabbooking.service.IAdminVerificationService;
import com.cabbooking.service.IDriverService;

/**
 * REST controller for handling admin-specific operations.
 * 
 * Main Responsibilities: 
 * - Provides endpoints for viewing and verifying driver accounts. 
 * - Provides endpoints for viewing and verifying admin accounts. 
 * - Provides endpoints for viewing customer and driver summaries. 
 * - Provides endpoints for retrieving trip history by cab or date. 
 * 
 * Security: 
 * - All endpoints within this controller are secured using method-level security. 
 * - Only users with the 'Admin' role are authorized to access these functionalities.
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('Admin')")
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

    // Service layer injected to handle admin-related business logic.
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
        List<Driver> unverifiedDrivers = driverService.viewUnverifiedDrivers();
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
    public ResponseEntity<String> verifyDriver(@PathVariable int driverId) {
        logger.info("Admin trying to verify driver with id: {}", driverId);
        try {
            String message = driverService.verifyDriver(driverId);
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException e) {
            // Handles known issues like "driver not found"
            logger.warn("Driver verification failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Handles any unexpected server or database error
            logger.error("Unexpected error verifying driver", e);
            return ResponseEntity.internalServerError().body(e.getMessage());
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

    /**
     * Endpoint to get the trip history for a specific driver.
     *  
     * GET /api/admin/trips/driver/{driverId} 
     * 
     * Workflow: 
     * - An admin calls this endpoint to retrieve all trips associated with a specific driver. 
     * - Calls the adminService to fetch all trips for that driver from the database. 
     * - Returns the list of trips as a JSON array.
     *
     * @param driverId The ID of the driver.
     * @return A ResponseEntity containing the list of trips for that driver.
     */
    @GetMapping("/trips/driver/{driverId}")
    public ResponseEntity<List<TripBooking>> getTripsByDriver(@PathVariable Integer driverId) {
        logger.info("Admin requested trip history for driver with id: {}", driverId);
        List<TripBooking> trips = adminService.getTripsByDriver(driverId);
        logger.info("Found {} trips for driver with id: {}", trips.size(), driverId);
        return ResponseEntity.ok(trips);
    }

    /**
     * Endpoint to get all trips that occurred on a specific date. 
     * 
     * GET /api/admin/trips/date/{date} 
     * 
     * Workflow: 
     * - An admin calls this endpoint to retrieve all trips that started on a specific date. 
     * - Calls the adminService to fetch all trips for that date from the database. 
     * - Returns the list of trips as a JSON array.
     *
     * @param date The date in yyyy-MM-dd format.
     * @return A ResponseEntity containing the list of trips for that date.
     */
    @GetMapping("/trips/date/{date}")
    public ResponseEntity<List<TripBooking>> getTripsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        logger.info("Admin requested trip history for date: {}", date);
        List<TripBooking> trips = adminService.getTripsByDate(date);
        logger.info("Found {} trips for date: {}", trips.size(), date);
        return ResponseEntity.ok(trips);
    }
}
