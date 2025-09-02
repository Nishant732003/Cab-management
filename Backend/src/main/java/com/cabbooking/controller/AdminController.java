package com.cabbooking.controller;

import java.util.List;

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
 * (Your existing comments are preserved)
 */
@RestController
@RequestMapping("/api/admin")
@Validated
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private IAdminService adminService;
    
    @Autowired
    private IAdminVerificationService verificationService;

    @Autowired
    private IDriverService driverService;

    /**
     * Constructor for AdminController.
     * (Your existing comments are preserved)
     */
    public AdminController() {
        logger.info("**************************************************");
        logger.info("********** AdminController is being created **********");
        logger.info("**************************************************");
    }

    /**
     * Endpoint to retrieve a summary of all customer accounts.
     * (Your existing comments are preserved)
     */
    @GetMapping("/customers")
    public ResponseEntity<List<UserSummaryDTO>> getAllCustomers() {
        logger.info("Admin requested list of all customers");
        List<UserSummaryDTO> customers = adminService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    // --- FIX: ADD THE MISSING ENDPOINT TO GET ALL DRIVERS ---
    /**
     * Endpoint to retrieve a summary of all driver accounts.
     * * GET /api/admin/drivers
     * @return HTTP 200 with a List of UserSummaryDTOs for all drivers.
     */
    @GetMapping("/drivers")
    public ResponseEntity<List<UserSummaryDTO>> getAllDrivers() {
        logger.info("Admin requested list of all drivers");
        List<UserSummaryDTO> drivers = adminService.getAllDrivers();
        return ResponseEntity.ok(drivers);
    }

    /**
     * Endpoint to retrieve all unverified admin accounts.
     * (Your existing methods and comments are preserved)
     */
    @GetMapping("/unverified/admins")
    public ResponseEntity<List<Admin>> getUnverifiedAdmins() {
        logger.info("Superadmin requested list of unverified admins");
        List<Admin> unverifiedAdmins = verificationService.getUnverifiedAdmins();
        return ResponseEntity.ok(unverifiedAdmins);
    }

    /**
     * Endpoint to verify an admin account by its unique ID.
     * (Your existing methods and comments are preserved)
     */
    @PostMapping("/verify/admins/{adminId}")
    public ResponseEntity<String> verifyAdmin(@PathVariable Integer adminId) {
        logger.info("Superadmin trying to verify admin with id: {}", adminId);
        try {
            verificationService.verifyAdmin(adminId);
            return ResponseEntity.ok("Admin verified successfully");
        } catch (IllegalArgumentException e) {
            logger.warn("Admin verification failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error verifying admin", e);
            return ResponseEntity.internalServerError().body("An error occurred");
        }
    }

    /**
     * Endpoint to retrieve a list of all drivers awaiting verification.
     * (Your existing methods and comments are preserved)
     */
    @GetMapping("/unverified/drivers")
    public ResponseEntity<List<Driver>> getUnverifiedDrivers() {
        List<Driver> unverifiedDrivers = driverService.viewUnverifiedDrivers();
        return ResponseEntity.ok(unverifiedDrivers);
    }

    /**
     * Endpoint to verify a specific driver by their ID.
     * (Your existing methods and comments are preserved)
     */
    @PostMapping("/verify/drivers/{driverId}")
    public ResponseEntity<Driver> verifyDriver(@PathVariable int driverId) {
        Driver verifiedDriver = driverService.verifyDriver(driverId);
        return ResponseEntity.ok(verifiedDriver);
    }
}