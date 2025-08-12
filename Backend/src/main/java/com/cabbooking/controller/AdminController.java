package com.cabbooking.controller;

import com.cabbooking.model.Driver;
import com.cabbooking.service.IDriverService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('Admin')") // Ensure only Admins can access these endpoints
@Validated
public class AdminController {

    @Autowired
    private IDriverService driverService;

    @PostMapping("/drivers/{driverId}/assign-cab/{cabId}")
    public ResponseEntity<Driver> assignCab(@PathVariable int driverId, @PathVariable int cabId) {
        Driver updatedDriver = driverService.assignCabToDriver(driverId, cabId);
        return ResponseEntity.ok(updatedDriver);
    }

    // We will add other admin-specific endpoints here, like viewing best drivers, verifying drivers, etc.
}
