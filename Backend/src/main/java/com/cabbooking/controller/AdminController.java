package com.cabbooking.controller;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
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
import com.cabbooking.model.TripBooking;
import com.cabbooking.service.IAdminService;
import com.cabbooking.service.IAdminVerificationService;
import com.cabbooking.service.IDriverService;

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

    public AdminController() {
        logger.info("**************************************************");
        logger.info("********** AdminController is being created **********");
        logger.info("**************************************************");
    }

    /**
     * Get all customers.
     */
    @GetMapping("/customers")
    public ResponseEntity<List<UserSummaryDTO>> getAllCustomers() {
        logger.info("Fetching list of all customers");
        try {
            List<UserSummaryDTO> customers = adminService.getAllCustomers();
            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            logger.error("Error while fetching customers: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    /**
     * Get all drivers.
     */
    @GetMapping("/drivers")
    public ResponseEntity<List<UserSummaryDTO>> getAllDrivers() {
        logger.info("Fetching list of all drivers");
        try {
            List<UserSummaryDTO> drivers = adminService.getAllDrivers();
            return ResponseEntity.ok(drivers);
        } catch (Exception e) {
            logger.error("Error while fetching drivers: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all unverified admins.
     */
    @GetMapping("/unverified/admins")
    public ResponseEntity<List<Admin>> getUnverifiedAdmins() {
        logger.info("Superadmin requested list of unverified admins");
        try {
            List<Admin> unverifiedAdmins = verificationService.getUnverifiedAdmins();
            return ResponseEntity.ok(unverifiedAdmins);
        } catch (Exception e) {
            logger.error("Error while fetching unverified admins: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Verify and admin by ID.
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
     * Get all unverified drivers.
     */
    @GetMapping("/unverified/drivers")
    public ResponseEntity<List<Driver>> getUnverifiedDrivers() {
        logger.info("Fetching list of unverified drivers");
        try {
            List<Driver> unverifiedDrivers = driverService.viewUnverifiedDrivers();
            return ResponseEntity.ok(unverifiedDrivers);
        } catch (Exception e) {
            logger.error("Error while fetching unverified drivers: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/verify/drivers/{driverId}")
    public ResponseEntity<?> verifyDriver(@PathVariable int driverId) {
        logger.info("Attempting to verify driver with ID: {}", driverId);
        try {
            Driver verifiedDriver = driverService.verifyDriver(driverId);
            logger.info("Driver with ID {} verified successfully", driverId);
            return ResponseEntity.ok(verifiedDriver);
        } catch (IllegalArgumentException e) {
            logger.warn("Verification failed for driver ID {}: {}", driverId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error verifying driver ID {}: {}", driverId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("An unexpected error occurred while verifying driver");
        }
    }

    /**
     * Handles the GET request to retrieve all trips for a specific driver.
     *
     * @param driverId The ID of the driver.
     * @return A ResponseEntity containing a list of trips and an HTTP status code.
     */
    @GetMapping("/trips/driver/{driverId}")
    public ResponseEntity<List<TripBooking>> getTripsByDriver(@PathVariable int driverId) {
        logger.info("Admin requested trips for driver with ID: {}", driverId);
        try {
            List<TripBooking> trips = adminService.getTripsByDriver(driverId);
            return ResponseEntity.ok(trips);
        } catch (Exception e) {
            logger.error("An error occurred while fetching trips for driver with ID {}: {}", driverId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Handles the GET request to retrieve all trips for a specific date.
     *
     * @param date The date for which to retrieve trips.
     * @return A ResponseEntity containing a list of trips and an HTTP status code.
     */
    @GetMapping("/trips/date/{date}")
    public ResponseEntity<List<TripBooking>> getTripsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        logger.info("Admin requested trips for date: {}", date);
        try {
            List<TripBooking> trips = adminService.getTripsByDate(date);
            return ResponseEntity.ok(trips);
        } catch (Exception e) {
            logger.error("An error occurred while fetching trips for date {}: {}", date, e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
