package com.cabbooking.controller;

import java.security.Principal;
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
 * REST Controller for handling all HTTP requests related to Trip Bookings.
 * 
 * This controller acts as the entry point for all trip-related actions from the
 * client-side, such as booking a trip, updating its status, and viewing trip
 * history.
 * 
 * It delegates the business logic to the {@link ITripBookingService}.
 *
 * Main Responsibilities:
 * - Provides endpoints for booking, updating, and completing trips.
 * - Handles security using method-level security.
 *
 * Security:
 * - All endpoints within this controller are secured using method-level security.
 * - Only Authenticated users can access these endpoints.
 */
@RestController
@RequestMapping("/api/trips")
@PreAuthorize("isAuthenticated()")
public class TripBookingController {

    // SLF4J Logger for tracking requests and actions in this controller
    private static final Logger logger = LoggerFactory.getLogger(TripBookingController.class);

    // Service layer for handling trip-related business logic
    @Autowired
    private ITripBookingService tripBookingService;

    // Service layer for handling cab-related business logic
    @Autowired
    private ICabService cabService;

    /**
     * Endpoint to get a list of fare estimates for nearby and available car types.
     * 
     * GET /api/trips/estimate
     * 
     * Workflow:
     * - Used by the customer to get a list of fare estimates for nearby and available car types.
     * - Calls the service layer to calculate the estimates.
     * - Returns a ResponseEntity containing the list of fare estimates.
     *
     * @param distance The estimated distance of the trip in kilometers.
     * @param lat The customer's current latitude.
     * @param lng The customer's current longitude.
     * @return A ResponseEntity containing a list of fare estimates.
     */
    @GetMapping("/estimate")
    @PreAuthorize("hasRole('Customer')")
    public ResponseEntity<List<FareEstimateResponse>> getFareEstimates(
            @RequestParam float distance,
            @RequestParam double lat,
            @RequestParam double lng) {
        logger.info("Received request to get fare estimates.");
        List<FareEstimateResponse> estimates = cabService.getAllFareEstimates(distance, lat, lng);
        logger.info("Retrieved fare estimates.");
        return ResponseEntity.ok(estimates);
    }

    /**
     * Endpoint for a customer to book a new trip.
     * 
     * POST /api/trips/book
     * 
     * Workflow:
     * - Used by the customer to book a new trip.
     * - Calls the service layer to book the trip.
     * - Returns a ResponseEntity containing the created TripBooking object.
     *
     * @param tripBookingRequest DTO containing the necessary details for the booking.
     * @return A ResponseEntity containing the created TripBooking object.
     */
    @PostMapping("/book")
    @PreAuthorize("hasRole('Customer')")
    public ResponseEntity<TripBooking> bookTrip(@Valid @RequestBody TripBookingRequest tripBookingRequest) {
        logger.info("Received request to book a new trip.");
        TripBooking newTrip = tripBookingService.bookTrip(tripBookingRequest);
        logger.info("Trip booked successfully.");
        return ResponseEntity.ok(newTrip);
    }

    /**
     * Endpoint to update the status of a trip.
     * 
     * Only driver can update the status.
     * 
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
    public ResponseEntity<TripBooking> updateTripStatus(@PathVariable Integer tripId, @RequestParam String status, Principal principal) {
        logger.info("Received request to update trip status.");
        TripBooking updatedTrip = tripBookingService.updateTripStatus(tripId, status, principal.getName());
        logger.info("Trip status updated successfully.");
        return ResponseEntity.ok(updatedTrip);
    }

    /**
     * Endpoint for a driver to mark a trip as complete.
     * This will also trigger the bill calculation.
     * 
     * PUT /api/trips/{tripId}/complete
     * 
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
    public ResponseEntity<TripBooking> completeTrip(@PathVariable Integer tripId, Principal principal) {
        logger.info("Received request to complete a trip.");
        TripBooking completedTrip = tripBookingService.completeTrip(tripId, principal.getName());
        logger.info("Trip completed successfully.");
        return ResponseEntity.ok(completedTrip);
    }

    /**
     * Endpoint for a customer to view their entire trip history.
     * 
     * GET /api/trips/customer/{customerId}
     * 
     * Workflow:
     * - Used by a customer to view their entire trip history.
     * - Calls the service layer to fetch the customer's trip history.
     * - Returns a ResponseEntity containing a list of the customer's past and present trips.
     *
     * @param customerId The ID of the customer.
     * @return A list of the customer's past and present trips.
     */
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('Customer') or hasRole('Admin')")
    public ResponseEntity<List<TripBooking>> getCustomerTrips(@PathVariable Integer customerId) {
        logger.info("Received request to view customer's trip history.");
        List<TripBooking> trips = tripBookingService.getAllTripsCustomer(customerId);
        logger.info("Retrieved customer's trip history.");
        return ResponseEntity.ok(trips);
    }

    /**
     * Endpoint for a customer to rate a completed trip.
     *
     * @param tripId The ID of the trip being rated.
     * @param ratingRequest The rating details.
     * @param principal The currently authenticated user, injected by Spring Security.
     * @return The rated trip object.
     */
    @PostMapping("/{tripId}/rate")
    @PreAuthorize("hasRole('Customer')")
    public ResponseEntity<TripBooking> rateTrip(@PathVariable Integer tripId,
                                                @Valid @RequestBody RatingRequest ratingRequest,
                                                Principal principal) {
        
        logger.info("Customer '{}' trying to rate trip ID: {}", principal.getName(), tripId);

        TripBooking ratedTrip = tripBookingService.rateTrip(tripId, ratingRequest, principal.getName());

        logger.info("Trip rated successfully.");
        return ResponseEntity.ok(ratedTrip);
    }
}
