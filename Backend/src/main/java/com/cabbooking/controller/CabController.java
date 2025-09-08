package com.cabbooking.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
 * REST controller for handling cab-related operations.
 *
 * Endpoints:
 * - PUT /api/cabs/{driverId}/update: Admin can add or update cab details for a specific driver.
 * - GET /api/cabs/type/{carType}: View all cabs of a specific type.
 * - GET /api/cabs/{cabId}: Get details of a specific cab by its ID.
 * - GET /api/cabs/all: Admin can view all cabs in the system.
 * - GET /api/cabs/available: Admin can view all available cabs in the system.
 * - PUT /api/cabs/{cabId}/image: Driver can upload or update a cab's image.
 * - DELETE /api/cabs/{cabId}/image: Admin can remove a cab's image.
 * 
 * Main Responsibilities:
 * - Handle HTTP requests related to cab management.
 * - Delegate business logic to the ICabService.
 * - Return appropriate HTTP responses with status codes and data.
 * 
 * Dependencies:
 * - ICabService: Service layer for cab-related operations.
 */
@RestController
@RequestMapping("/api/cabs")
public class CabController {

    // SLF4J Logger for tracking requests and actions in this controller
    private static final Logger logger = LoggerFactory.getLogger(CabController.class);

    // Service layer injected to handle cab-related business logic.
    @Autowired
    private ICabService cabService;

    /**
     * Endpoint for an admin to add or update the details of a cab for a
     * specific driver.
     *
     * PUT /api/cabs/{driverId}/update
     *
     * Workflow:
     * - User sends request with driver ID and CabUpdateRequest DTO.
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
        logger.info("Received request to update cab details for a driver.");
        Cab updatedCab = cabService.updateCabDetails(driverId, request);
        logger.info("Updated cab details for driver with ID: {}", driverId);
        return ResponseEntity.ok(updatedCab);
    }

    /**
     * Endpoint to view all cabs of a specific type.
     *
     * GET /api/cabs/type/{carType}
     *
     * Workflow:
     * - Accepts a car type as a path variable.
     * - Calls the service layer to retrieve a list of cabs matching the type.
     * - Returns a ResponseEntity containing the list of cabs.
     *
     * @param carType The type of car to filter by (e.g., "Sedan", "SUV").
     * @return A ResponseEntity containing a list of cabs matching the type.
     */
    @GetMapping("/type/{carType}")
    public ResponseEntity<List<Cab>> getCabsOfType(@PathVariable String carType) {
        logger.info("Received request to view cabs of a specific type.");
        List<Cab> cabs = cabService.getCabsOfType(carType);
        logger.info("Retrieved cabs of type: {}", carType);
        return ResponseEntity.ok(cabs);
    }

    /**
     * Endpoint to get the details of a specific cab by its ID.
     *
     * GET /api/cabs/{cabId}
     *
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
        Optional<Cab> cab = cabService.getCabById(cabId);
        if (cab.isPresent()) {
            logger.info("Retrieved details of cab with ID: {}", cabId);
            return ResponseEntity.ok(cab.get());
        } else {
            logger.info("Cab with ID: {} not found.", cabId);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint to retrieve all cabs in the system.
     *
     * GET /api/cabs/all
     *
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
        List<Cab> cabs = cabService.getAllCabs();
        logger.info("Retrieved all cabs.");
        return ResponseEntity.ok(cabs);
    }

    /**
     * Endpoint to retrieve all available cabs in the system.
     *
     * GET /api/cabs/available
     *
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
        List<Cab> cabs = cabService.getAllAvailableCabs();
        logger.info("Retrieved all available cabs.");
        return ResponseEntity.ok(cabs);
    }

    /**
     * Endpoint for an driver to upload or update a cab's image. If an image
     * already exists, it will be replaced.
     *
     * PUT /api/cabs/{cabId}/image
     *
     * Workflow:
     * - Accepts a cab ID as a path variable.
     * - Accepts an image file as a request parameter.
     * - Calls the service layer to upload or update the cab's image.
     * - Returns a ResponseEntity containing the updated Cab object.
     *
     * @param cabId The ID of the cab.
     * @param file The image file.
     * @return The updated Cab object.
     */
    @PutMapping("/{cabId}/image")
    public ResponseEntity<Cab> uploadOrUpdateCabImage(@PathVariable int cabId, @RequestParam("file") MultipartFile file) {
        logger.info("Received request to upload or update a cab's image.");
        try {
            Cab updatedCab = cabService.uploadImage(cabId, file);
            logger.info("Updated cab's image.");
            return ResponseEntity.ok(updatedCab);
        } catch (IOException e) {
            logger.error("Failed to upload or update cab's image.");
            return ResponseEntity.internalServerError().build();
        } catch (IllegalArgumentException e) {
            logger.error("Cab with ID: {} not found.", cabId);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint for an admin to remove a cab's image.
     *
     * DELETE /api/cabs/{cabId}/image
     *
     * Workflow:
     * - Accepts a cab ID as a path variable.
     * - Calls the service layer to remove the cab's image.
     * - Returns a ResponseEntity containing the updated Cab object.
     *
     * @param cabId The ID of the cab.
     * @return The updated Cab object.
     */
    @DeleteMapping("/{cabId}/image")
    public ResponseEntity<Cab> removeCabImage(@PathVariable int cabId) {
        logger.info("Received request to remove a cab's image.");
        try {
            Cab updatedCab = cabService.removeImage(cabId);
            logger.info("Removed cab's image.");
            return ResponseEntity.ok(updatedCab);
        } catch (IOException e) {
            logger.error("Failed to remove cab's image.");
            return ResponseEntity.internalServerError().build();
        } catch (IllegalArgumentException e) {
            logger.error("Cab with ID: {} not found.", cabId);
            return ResponseEntity.notFound().build();
        }
    }
}
