package com.cabbooking.controller;

import com.cabbooking.model.Driver;
import com.cabbooking.service.IDriverService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('Admin')") // Ensure only Admins can access these endpoints
@Validated
public class AdminController {

    @Autowired
    private IDriverService driverService;

    // We will add other admin-specific endpoints here, like viewing best drivers, verifying drivers, etc.
    // ==> ADD THESE NEW DRIVER VERIFICATION ENDPOINTS <==

    @GetMapping("/drivers/unverified")
    public ResponseEntity<List<Driver>> getUnverifiedDrivers() {
        List<Driver> unverifiedDrivers = driverService.viewUnverifiedDrivers();
        return ResponseEntity.ok(unverifiedDrivers);
    }

    @PostMapping("/drivers/{driverId}/verify")
    public ResponseEntity<Driver> verifyDriver(@PathVariable int driverId) {
        Driver verifiedDriver = driverService.verifyDriver(driverId);
        return ResponseEntity.ok(verifiedDriver);
    }
}
