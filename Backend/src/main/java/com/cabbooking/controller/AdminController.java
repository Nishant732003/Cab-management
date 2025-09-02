package com.cabbooking.controller;

import java.time.LocalDate; // <-- IMPORT THIS
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat; // <-- IMPORT THIS
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
import com.cabbooking.model.TripBooking; // <-- IMPORT THIS
import com.cabbooking.service.IAdminService;
import com.cabbooking.service.IAdminVerificationService;
import com.cabbooking.service.IDriverService;
import com.cabbooking.service.ITripBookingService;

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

    @Autowired
    private ITripBookingService tripBookingService;

    public AdminController() {
        logger.info("**************************************************");
        logger.info("********** AdminController is being created **********");
        logger.info("**************************************************");
    }

    @GetMapping("/customers")
    public ResponseEntity<List<UserSummaryDTO>> getAllCustomers() {
        logger.info("Admin requested list of all customers");
        List<UserSummaryDTO> customers = adminService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/drivers")
    public ResponseEntity<List<UserSummaryDTO>> getAllDrivers() {
        logger.info("Admin requested list of all drivers");
        List<UserSummaryDTO> drivers = adminService.getAllDrivers();
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/unverified/admins")
    public ResponseEntity<List<Admin>> getUnverifiedAdmins() {
        logger.info("Superadmin requested list of unverified admins");
        List<Admin> unverifiedAdmins = verificationService.getUnverifiedAdmins();
        return ResponseEntity.ok(unverifiedAdmins);
    }

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

    @GetMapping("/unverified/drivers")
    public ResponseEntity<List<Driver>> getUnverifiedDrivers() {
        List<Driver> unverifiedDrivers = driverService.viewUnverifiedDrivers();
        return ResponseEntity.ok(unverifiedDrivers);
    }

    @PostMapping("/verify/drivers/{driverId}")
    public ResponseEntity<Driver> verifyDriver(@PathVariable int driverId) {
        Driver verifiedDriver = driverService.verifyDriver(driverId);
        return ResponseEntity.ok(verifiedDriver);
    }

    @GetMapping("/trips/driver/{driverId}")
    public ResponseEntity<List<TripBooking>> getTripsByDriver(@PathVariable int driverId) {
        logger.info("Admin requested trips for driver with ID: {}", driverId);
        List<TripBooking> trips = tripBookingService.viewAllTripsDriver(driverId);
        return ResponseEntity.ok(trips);
    }

    @GetMapping("/trips/date/{date}")
    public ResponseEntity<List<TripBooking>> getTripsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        logger.info("Admin requested trips for date: {}", date);
        List<TripBooking> trips = tripBookingService.getTripsDatewise(date);
        return ResponseEntity.ok(trips);
    }
}