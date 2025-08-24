package com.cabbooking.controller;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cabbooking.dto.CabUpdateRequest;
import com.cabbooking.model.Cab;
import com.cabbooking.service.ICabService;

import jakarta.validation.Valid;

/**
 * REST controller for admin operations related to cab management. 
 * Main Responsibilities: 
 * - Provides CRUD (Create, Read, Update, Delete) endpoints for managing cabs. 
 * - Allows admins to view and count cabs based on their type. 
 * 
 * Security: 
 * - All endpoints are secured and require the user to have the 'Admin' role.
 */
@RestController
@RequestMapping("/api/cabs")
@PreAuthorize("hasRole('Admin') or hasRole('Driver')")
public class CabController {

    // SLF4J Logger for tracking requests and actions in this controller
    private static final Logger logger = LoggerFactory.getLogger(CabController.class);

    // Service layer injected to handle cab-related business logic.
    @Autowired
    private ICabService cabService;

    /**
     * Endpoint for an admin to add or update the details of a cab for a specific driver.
     * PUT /api/cabs/drivers/{driverId}/cab
     * 
     * Workflow:
     * - Accepts a driver ID as a path variable.
     * - Calls the service layer to update the cab details for the driver.
     * - Returns the updated Cab object wrapped in a ResponseEntity.
     * 
     * @param driverId The ID of the driver whose cab details are being set.
     * @param request The DTO with the new cab details.
     * @return The updated Cab object.
     */
    @PutMapping("/{driverId}/update")
    public ResponseEntity<Cab> updateCabForDriver(
            @PathVariable int driverId,
            @Valid @RequestBody CabUpdateRequest request) {
        
        Cab updatedCab = cabService.updateCabDetails(driverId, request);
        return ResponseEntity.ok(updatedCab);
    }

    /**
     * Endpoint to delete a cab from the system by its ID. 
     * DELETE /api/cabs/delete/{cabId} 
     * Workflow: 
     * - Accepts a cab ID as a path variable.
     * - Calls the service layer to delete the cab from the database. 
     * - Returns the deleted Cab object wrapped in a ResponseEntity.
     *
     * @param cabId The unique ID of the cab to be deleted.
     * @return A ResponseEntity containing the cab that was deleted.
     */
    @DeleteMapping("/delete/{cabId}")
    public ResponseEntity<Cab> deleteCab(@PathVariable int cabId) {
        logger.info("Received request to delete a cab.");
        Cab deletedCab = cabService.deleteCab(cabId);
        return ResponseEntity.ok(deletedCab);
    }

    /**
     * Endpoint to view all cabs of a specific type. 
     * GET /api/cabs/view/{carType} 
     * Workflow: 
     * - Accepts a car type as a path variable. 
     * - Calls the service layer to retrieve a list of cabs matching the type. 
     * - Returns a ResponseEntity containing the list of cabs.
     *
     * @param carType The type of car to filter by (e.g., "Sedan", "SUV").
     * @return A ResponseEntity containing a list of cabs matching the type.
     */
    @GetMapping("/view/{carType}")
    public ResponseEntity<List<Cab>> viewCabsOfType(@PathVariable String carType) {
        logger.info("Received request to view cabs of a specific type.");
        List<Cab> cabs = cabService.viewCabsOfType(carType);
        return ResponseEntity.ok(cabs);
    }

    /**
     * Endpoint to count the number of available cabs of a specific type. 
     * GET /api/cabs/count/{carType} 
     * Workflow: 
     * - Accepts a car type as a path variable. 
     * - Calls the service layer to count the cabs of that type. 
     * - Returns a ResponseEntity containing the count as an integer.
     *
     * @param carType The type of car to count.
     * @return A ResponseEntity containing the total count as an integer.
     */
    @GetMapping("/count/{carType}")
    public ResponseEntity<Integer> countCabsOfType(@PathVariable String carType) {
        logger.info("Received request to count cabs of a specific type.");
        int count = cabService.countCabsOfType(carType);
        return ResponseEntity.ok(count);
    }

    /**
     * Endpoint to get the details of a specific cab by its ID. 
     * GET /api/cabs/{cabId} 
     * Workflow: 
     * - Accepts a cab ID as a path variable. 
     * - Calls the service layer to retrieve the cab details. 
     * - Returns a ResponseEntity containing the Cab object if found, or a 404 Not Found response if not.
     *
     * @param cabId The unique ID of the cab to retrieve.
     * @return A ResponseEntity containing the Cab object if found, or a 404 Not
     * Found response.
     */
    @GetMapping("/{cabId}")
    public ResponseEntity<Cab> getCabById(@PathVariable int cabId) {
        logger.info("Received request to get details of a specific cab.");
        return cabService.viewCab(cabId)
                .map(cab -> ResponseEntity.ok(cab)) // If cab is found, wrap it in a 200 OK response
                .orElse(ResponseEntity.notFound().build()); // If not found, return a 404 Not Found
    }

    /**
     * Endpoint to retrieve all cabs in the system. 
     * GET /api/cabs/all 
     * Workflow:
     * - An admin calls this endpoint to get a list of all cabs in the system. 
     * - Calls the service layer to fetch all cabs from the database. 
     * - Returns a ResponseEntity containing the list of cabs as a JSON array.
     *
     * @return A ResponseEntity containing a list of all Cab objects.
     */
    @GetMapping("/all")
    public ResponseEntity<List<Cab>> getAllCabs() {
        logger.info("Received request to get all cabs.");
        List<Cab> cabs = cabService.viewAllCabs();
        return ResponseEntity.ok(cabs);
    }

    /**
     * Endpoint to retrieve all available cabs in the system. 
     * GET /api/cabs/available 
     * Workflow: 
     * - An admin calls this endpoint to get a list of all available cabs in the system. 
     * - Calls the service layer to fetch all available cabs from the database. 
     * - Returns a ResponseEntity containing the list of available cabs as a JSON array.
     *
     * @return A ResponseEntity containing a list of all available Cab objects.
     */
    @GetMapping("/available")
    public ResponseEntity<List<Cab>> getAllAvailableCabs() {
        logger.info("Received request to get all available cabs.");
        List<Cab> cabs = cabService.viewAllAvailableCabs();
        return ResponseEntity.ok(cabs);
    }

    /**
     * Endpoint for an admin to upload or update a cab's image.
     * If an image already exists, it will be replaced.
     *
     * @param cabId The ID of the cab.
     * @param file The image file.
     * @return The updated Cab object.
     */
    @PutMapping("/{cabId}/image")
    public ResponseEntity<Cab> uploadOrUpdateCabImage(@PathVariable int cabId, @RequestParam("file") MultipartFile file) {
        try {
            Cab updatedCab = cabService.uploadImage(cabId, file);
            return ResponseEntity.ok(updatedCab);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint for an admin to remove a cab's image.
     *
     * @param cabId The ID of the cab.
     * @return The updated Cab object.
     */
    @DeleteMapping("/{cabId}/image")
    public ResponseEntity<Cab> removeCabImage(@PathVariable int cabId) {
        try {
            Cab updatedCab = cabService.removeImage(cabId);
            return ResponseEntity.ok(updatedCab);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
