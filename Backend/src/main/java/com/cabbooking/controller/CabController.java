package com.cabbooking.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cabbooking.model.Cab;
import com.cabbooking.service.ICabService;

/**
 * REST controller for admin operations related to cab management.
 *
 * Main Responsibilities:
 * - Provides CRUD (Create, Read, Update, Delete) endpoints for managing cabs.
 * - Allows admins to view and count cabs based on their type.
 *
 * Security:
 * - All endpoints are secured and require the user to have the 'Admin' role.
 */
@RestController
@RequestMapping("/api/cabs")
@PreAuthorize("hasRole('Admin')")
public class CabController {

    // Service layer injected to handle cab-related business logic.
    @Autowired
    private ICabService cabService;

    /**
     * Endpoint to add a new cab to the system.
     *
     * POST /api/cabs/add
     *
     * @param cab The Cab object to be created, sent in the request body.
     * @return A ResponseEntity containing the newly created Cab object.
     */
    @PostMapping("/add")
    public ResponseEntity<Cab> addCab(@RequestBody Cab cab) {
        Cab newCab = cabService.insertCab(cab);
        return ResponseEntity.ok(newCab);
    }

    /**
     * Endpoint to update an existing cab's details.
     *
     * PUT /api/cabs/update
     *
     * @param cab The Cab object with updated information.
     * @return A ResponseEntity containing the updated Cab object.
     */
    @PutMapping("/update")
    public ResponseEntity<Cab> updateCab(@RequestBody Cab cab) {
        Cab updatedCab = cabService.updateCab(cab);
        return ResponseEntity.ok(updatedCab);
    }

    /**
     * Endpoint to delete a cab from the system by its ID.
     *
     * DELETE /api/cabs/delete/{cabId}
     *
     * @param cabId The unique ID of the cab to be deleted.
     * @return A ResponseEntity containing the cab that was deleted.
     */
    @DeleteMapping("/delete/{cabId}")
    public ResponseEntity<Cab> deleteCab(@PathVariable int cabId) {
        Cab deletedCab = cabService.deleteCab(cabId);
        return ResponseEntity.ok(deletedCab);
    }

    /**
     * Endpoint to view all cabs of a specific type.
     *
     * GET /api/cabs/view/{carType}
     *
     * @param carType The type of car to filter by (e.g., "Sedan", "SUV").
     * @return A ResponseEntity containing a list of cabs matching the type.
     */
    @GetMapping("/view/{carType}")
    public ResponseEntity<List<Cab>> viewCabsOfType(@PathVariable String carType) {
        List<Cab> cabs = cabService.viewCabsOfType(carType);
        return ResponseEntity.ok(cabs);
    }

    /**
     * Endpoint to count the number of available cabs of a specific type.
     *
     * GET /api/cabs/count/{carType}
     *
     * @param carType The type of car to count.
     * @return A ResponseEntity containing the total count as an integer.
     */
    @GetMapping("/count/{carType}")
    public ResponseEntity<Integer> countCabsOfType(@PathVariable String carType) {
        int count = cabService.countCabsOfType(carType);
        return ResponseEntity.ok(count);
    }

    /**
     * Endpoint to get the details of a specific cab by its ID.
     *
     * GET /api/cabs/{cabId}
     *
     * @param cabId The unique ID of the cab to retrieve.
     * @return A ResponseEntity containing the Cab object if found, or a 404 Not Found response.
     */
    @GetMapping("/{cabId}")
    public ResponseEntity<Cab> getCabById(@PathVariable int cabId) {
        return cabService.viewCab(cabId)
                .map(cab -> ResponseEntity.ok(cab)) // If cab is found, wrap it in a 200 OK response
                .orElse(ResponseEntity.notFound().build()); // If not found, return a 404 Not Found
    }
}