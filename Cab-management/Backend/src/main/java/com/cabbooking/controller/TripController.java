package com.cabbooking.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
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
import com.cabbooking.dto.TripHistoryResponse;
import com.cabbooking.model.TripBooking; // Already imported, which is correct
import com.cabbooking.service.ICabService;
import com.cabbooking.service.ITripBookingService;

import jakarta.validation.Valid;

/**
 * REST Controller for handling trip-related operations.
 * 
 * Endpoints:
 * - GET /api/trips/estimate - Get fare estimates for nearby and available car types.
 * - POST /api/trips/book - Book a new trip.
 * - PUT /api/trips/{tripId}/status - Update the status of a trip (Driver only).
 * - PUT /api/trips/{tripId}/complete - Mark a trip as complete (Driver only).
 * - GET /api/trips/customer/{customerId} - View a customer's entire trip history.
 * - POST /api/trips/{tripId}/rate - Rate a completed trip.
 *
 * Main Responsibilities:
 * - Provides endpoints for booking, updating, and completing trips.
 * - Allows customers to view their trip history and rate completed trips.
 * - Interacts with service layers to perform business logic.
 * - Logs requests and actions for tracking.
 *
 * Dependencies:
 * - ITripBookingService: Service layer for trip-related operations.
 * - ICabService: Service layer for cab-related operations.
 */
@RestController
@RequestMapping("/api/trips")
public class TripController {

    // SLF4J Logger for tracking requests and actions in this controller
    private static final Logger logger = LoggerFactory.getLogger(TripController.class);

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
     * Endpoint to book or schedule a new trip.
     * 
     * POST /api/trips/book
     * 
     * Workflow:
     * - Used by the customer to book or schedule a new trip.
     * - Calls the service layer to book or schedule the trip.
     * - Returns a ResponseEntity containing the created TripBooking object.
     *
     * @param tripBookingRequest DTO containing the necessary details for the booking.
     * @return A ResponseEntity containing the created TripBooking object.
     */
    @PostMapping("/book")
    public ResponseEntity<TripBooking> bookTrip(@Valid @RequestBody TripBookingRequest tripBookingRequest) {
        logger.info("Received request to book a new trip.");
        TripBooking newTrip = tripBookingService.bookTrip(tripBookingRequest);
        logger.info("Trip booked successfully.");
        return ResponseEntity.ok(newTrip);
    }

    /**
     * Endpoint to update the status of a trip.
     * 
     * PUT /api/trips/{tripId}/status
     * 
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
    public ResponseEntity<TripBooking> updateTripStatus(@PathVariable Integer tripId, @RequestParam String status, Principal principal) {
        logger.info("Received request to update trip status.");
        TripBooking updatedTrip = tripBookingService.updateTripStatus(tripId, status, principal.getName());
        logger.info("Trip status updated successfully.");
        return ResponseEntity.ok(updatedTrip);
    }

    /**
     * Endpoint to mark a trip as complete.
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
    public ResponseEntity<TripBooking> completeTrip(@PathVariable Integer tripId, Principal principal) {
        logger.info("Received request to complete a trip.");
        TripBooking completedTrip = tripBookingService.completeTrip(tripId, principal.getName());
        logger.info("Trip completed successfully.");
        return ResponseEntity.ok(completedTrip);
    }
  
     /**
     * Endpoint to view a driver's entire trip history.
     * 
     * GET /api/trips/driver/{driverId}
     * 
     * Workflow:
     * - A driver calls this endpoint to retrieve their entire trip history.
     * - Calls the tripBookingService to fetch all trips for the driver from the database.
     * - Returns the list of trips as a JSON array.
     *
     * @param driverId The ID of the driver.
     * @param principal The currently authenticated user.
     * @return A list of the driver's past and present trips.
     */
    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<TripHistoryResponse>> getDriverTrips(@PathVariable Integer driverId, Principal principal) {
        logger.info("Driver '{}' requested trip history for driverId: {}", principal.getName(), driverId);
        // Change the list type from TripBooking to TripHistoryResponse
        List<TripHistoryResponse> trips = tripBookingService.getTripsByDriver(driverId);
        logger.info("Found {} trips for driverId: {}", trips.size(), driverId);
        return ResponseEntity.ok(trips);
    }

    /**
     * Endpoint to view a customer's entire trip history.
     * 
     * GET /api/trips/customer/{customerId}
     * 
     * Workflow:
     * - A customer calls this endpoint to retrieve their entire trip history.
     * - Calls the tripBookingService to fetch all trips for the customer from the database.
     * - Returns the list of trips as a JSON array.
     *
     * @param customerId The ID of the customer.
     * @return A list of the customer's past and present trips.
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<TripHistoryResponse>> getCustomerTrips(@PathVariable Integer customerId) {
        logger.info("Customer requested trip history for customerId: {}", customerId);
        // Change the list type from TripBooking to TripHistoryResponse
        List<TripHistoryResponse> trips = tripBookingService.getAllTripsCustomer(customerId);
        logger.info("Found {} trips for customerId: {}", trips.size(), customerId);
        return ResponseEntity.ok(trips);
    }

    /**
     * Endpoint to get all trips that occurred on a specific date. 
     * 
     * GET /api/admin/trips/date/{date} 
     * 
     * Workflow: 
     * - An admin calls this endpoint to retrieve all trips that started on a specific date. 
     * - Calls the tripBookingService to fetch all trips for that date from the database. 
     * - Returns the list of trips as a JSON array.
     *
     * @param date The date in yyyy-MM-dd format.
     * @return A ResponseEntity containing the list of trips for that date.
     */
    @GetMapping("/trips/date/{date}")
    public ResponseEntity<List<TripHistoryResponse>> getTripsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        logger.info("Admin requested trip history for date: {}", date);
        List<TripHistoryResponse> trips = tripBookingService.getTripsByDate(date);
        logger.info("Found {} trips for date: {}", trips.size(), date);
        return ResponseEntity.ok(trips);
    }

    /**
     * Endpoint to rate a completed trip.
     * 
     * POST /api/trips/{tripId}/rate
     * 
     * Workflow:
     * - Used by a customer to rate a completed trip.
     * - Calls the service layer to rate the trip.
     * - Returns a ResponseEntity containing the rated trip object.
     *
     * @param tripId The ID of the trip being rated.
     * @param ratingRequest The rating details.
     * @param principal The currently authenticated user, injected by Spring Security.
     * @return The rated trip object.
     */
    @PostMapping("/{tripId}/rate")
    public ResponseEntity<TripBooking> rateTrip(@PathVariable Integer tripId,
                                                @Valid @RequestBody RatingRequest ratingRequest,
                                                Principal principal) {
        
        logger.info("Customer '{}' trying to rate trip ID: {}", principal.getName(), tripId);

        TripBooking ratedTrip = tripBookingService.rateTrip(tripId, ratingRequest, principal.getName());

        logger.info("Trip rated successfully.");
        return ResponseEntity.ok(ratedTrip);
    }
}