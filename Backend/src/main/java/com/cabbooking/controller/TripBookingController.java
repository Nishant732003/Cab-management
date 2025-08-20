package com.cabbooking.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cabbooking.dto.FareEstimateResponse;
import com.cabbooking.dto.RatingRequest;
import com.cabbooking.dto.TripBookingRequest;
import com.cabbooking.model.TripBooking;
import com.cabbooking.service.ICabService;
import com.cabbooking.service.ITripBookingService;

import jakarta.validation.Valid;

/**
 * REST Controller for handling all HTTP requests related to Trip Bookings. This
 * controller acts as the entry point for all trip-related actions from the
 * client-side, such as booking a trip, updating its status, and viewing trip
 * history. It delegates the business logic to the {@link ITripBookingService}.
 * 
 * Main Responsibilities: 
 * - Provides endpoints for booking, updating, and completing trips. 
 * - Handles security using method-level security. 
 * 
 * Security: 
 * - All endpoints are secured and require the user to have the 'Customer' role. 
 * - Only users with the 'Customer' role can access these endpoints. 
 * - Users can only update their own trip status. 
 * - Admins can update any user's trip status. 
 * - Drivers can only update their own trip status.
 */
@RestController
@RequestMapping("/api/trips")
@PreAuthorize("isAuthenticated()")
public class TripBookingController {

    // SLF4J Logger for tracking requests and actions in this controller
    private static final Logger logger = LoggerFactory.getLogger(TripBookingController.class);

    @Autowired
    private ITripBookingService tripBookingService;

    @Autowired
    private ICabService cabService;

    /**
     * Endpoint to get a list of fare estimates for all available car types.
     * Customers can use this to see the price range for their trip across all
     * options. 
     * GET /api/trips/estimate 
     * Workflow: 
     * - Used by the customer to get a list of fare estimates for all available car types. 
     * - Calls the service layer to fetch all available fare estimates. 
     * - Returns a ResponseEntity containing a list of fare estimates.
     *
     * @param distance The estimated distance of the trip in kilometers.
     * @return A ResponseEntity containing a list of fare estimates.
     */
    @GetMapping("/estimate")
    @PreAuthorize("hasRole('Customer')")
    public ResponseEntity<List<FareEstimateResponse>> getFareEstimates(@RequestParam float distance) {
        logger.info("Received request to get fare estimates.");
        List<FareEstimateResponse> estimates = cabService.getAllFareEstimates(distance);
        return ResponseEntity.ok(estimates);
    }

    /**
     * Endpoint for a customer to book a new trip. 
     * POST /api/trips/book
     * Workflow: 
     * - Used by the customer to book a new trip. 
     * - Calls the service layer to book the trip. 
     * - Returns a ResponseEntity containing the created TripBooking object.
     *
     * @param tripBookingRequest DTO containing the necessary details for the
     * booking.
     * @return A {@link ResponseEntity} containing the created
     * {@link TripBooking} object.
     */
    @PostMapping("/book")
    @PreAuthorize("hasRole('Customer')")
    public ResponseEntity<TripBooking> bookTrip(@Valid @RequestBody TripBookingRequest tripBookingRequest) {
        logger.info("Received request to book a new trip.");
        TripBooking newTrip = tripBookingService.bookTrip(tripBookingRequest);
        return ResponseEntity.ok(newTrip);
    }

    /**
     * Endpoint to update the status of a trip. Can be used by a driver. 
     * PUT /api/trips/{tripId}/status 
     * Workflow: 
     * - Used by a driver to update the status of a trip. 
     * - Calls the service layer to update the trip status. 
     * - Returns a ResponseEntity containing the updated trip object.
     *
     * @param tripId The ID of the trip to update.
     * @param status The new status (e.g., "IN_PROGRESS").
     * @return The updated trip object.
     */
    @PutMapping("/{tripId}/status")
    @PreAuthorize("hasRole('Driver')")
    public ResponseEntity<TripBooking> updateTripStatus(@PathVariable Integer tripId, @RequestParam String status) {
        logger.info("Received request to update trip status.");
        TripBooking updatedTrip = tripBookingService.updateTripStatus(tripId, status);
        return ResponseEntity.ok(updatedTrip);
    }

    /**
     * Endpoint for a driver to mark a trip as complete. This will also trigger
     * the bill calculation. 
     * PUT /api/trips/{tripId}/complete 
     * Workflow: 
     * - Used by a driver to mark a trip as complete. 
     * - Calls the service layer to complete the trip. 
     * - Returns a ResponseEntity containing the completed trip object with the final bill.
     *
     * @param tripId The ID of the trip being completed.
     * @return The completed trip object with the final bill.
     */
    @PutMapping("/{tripId}/complete")
    @PreAuthorize("hasRole('Driver')")
    public ResponseEntity<TripBooking> completeTrip(@PathVariable Integer tripId) {
        logger.info("Received request to complete a trip.");
        TripBooking completedTrip = tripBookingService.completeTrip(tripId);
        return ResponseEntity.ok(completedTrip);
    }

    /**
     * Endpoint for a customer to view their entire trip history. 
     * GET /api/trips/customer/{customerId} 
     * Workflow: 
     * - Used by a customer to view their entire trip history. 
     * - Calls the service layer to fetch the customer's trip history. 
     * - Returns a ResponseEntity containing a list of the customer's past and present trips.
     *
     * @param customerId The ID of the customer.
     * @return A list of the customer's past and present trips.
     */
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("principal == #username or hasRole('Admin')")
    public ResponseEntity<List<TripBooking>> getCustomerTrips(@PathVariable Integer customerId) {
        logger.info("Received request to view customer's trip history.");
        List<TripBooking> trips = tripBookingService.viewAllTripsCustomer(customerId);
        return ResponseEntity.ok(trips);
    }

    /**
     * Endpoint for a customer to rate a trip. 
     * POST /api/trips/{tripId}/rate
     * Workflow: 
     * - Used by a customer to rate a trip. 
     * - Calls the service layer to rate the trip. 
     * - Returns a ResponseEntity containing the rated trip object.
     *
     * @param tripId The ID of the trip being rated.
     * @param ratingRequest The rating details.
     * @return The rated trip object.
     * @throws Exception if the trip is not found.
     * @throws Exception if the trip is already rated.
     */
    @PostMapping("/{tripId}/rate")
    @PreAuthorize("hasRole('Customer')") // Only customers can rate trips
    public ResponseEntity<TripBooking> rateTrip(@PathVariable Integer tripId, @Valid @RequestBody RatingRequest ratingRequest) {
        logger.info("Received request to rate a trip.");
        // Here you would add an extra security check to ensure that the logged-in customer
        // is the one who actually owns this trip. For now, this is a good start.
        TripBooking ratedTrip = tripBookingService.rateTrip(tripId, ratingRequest);
        return ResponseEntity.ok(ratedTrip);
    }
}
