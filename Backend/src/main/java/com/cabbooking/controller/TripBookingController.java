package com.cabbooking.controller;

import java.util.List;

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
 * This controller acts as the entry point for all trip-related actions from the client-side,
 * such as booking a trip, updating its status, and viewing trip history.
 *
 * It delegates the business logic to the {@link ITripBookingService}.
 */
@RestController
@RequestMapping("/api/trips")
public class TripBookingController {

    @Autowired
    private ITripBookingService tripBookingService;

    @Autowired
    private ICabService cabService;
    
    /**
     * Endpoint to get a list of fare estimates for all available car types.
     * Customers can use this to see the price range for their trip across all options.
     *
     * @param distance The estimated distance of the trip in kilometers.
     * @return A ResponseEntity containing a list of fare estimates.
     */
    @GetMapping("/estimate")
    @PreAuthorize("hasRole('Customer')")
    public ResponseEntity<List<FareEstimateResponse>> getFareEstimates(@RequestParam float distance) {
        List<FareEstimateResponse> estimates = cabService.getAllFareEstimates(distance);
        return ResponseEntity.ok(estimates);
    }

    /**
     * Endpoint for a customer to book a new trip.
     *
     * @param tripBookingRequest DTO containing the necessary details for the booking.
     * @return A {@link ResponseEntity} containing the created {@link TripBooking} object.
     */
    @PostMapping("/book")
    public ResponseEntity<TripBooking> bookTrip(@Valid @RequestBody TripBookingRequest tripBookingRequest) {
        TripBooking newTrip = tripBookingService.bookTrip(tripBookingRequest);
        return ResponseEntity.ok(newTrip);
    }

    /**
     * Endpoint to update the status of a trip. Can be used by a driver.
     *
     * @param tripId The ID of the trip to update.
     * @param status The new status (e.g., "IN_PROGRESS").
     * @return The updated trip object.
     */
    @PutMapping("/{tripId}/status")
    public ResponseEntity<TripBooking> updateTripStatus(@PathVariable Integer tripId, @RequestParam String status) {
        TripBooking updatedTrip = tripBookingService.updateTripStatus(tripId, status);
        return ResponseEntity.ok(updatedTrip);
    }
    
    /**
     * Endpoint for a driver to mark a trip as complete.
     * This will also trigger the bill calculation.
     *
     * @param tripId The ID of the trip being completed.
     * @return The completed trip object with the final bill.
     */
    @PutMapping("/{tripId}/complete")
    public ResponseEntity<TripBooking> completeTrip(@PathVariable Integer tripId) {
        TripBooking completedTrip = tripBookingService.completeTrip(tripId);
        return ResponseEntity.ok(completedTrip);
    }

    /**
     * Endpoint for a customer to view their entire trip history.
     *
     * @param customerId The ID of the customer.
     * @return A list of the customer's past and present trips.
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<TripBooking>> getCustomerTrips(@PathVariable Integer customerId) {
        List<TripBooking> trips = tripBookingService.viewAllTripsCustomer(customerId);
        return ResponseEntity.ok(trips);
    }

    // ==> ADD THIS NEW ENDPOINT <==
    @PostMapping("/{tripId}/rate")
    @PreAuthorize("hasRole('Customer')") // Only customers can rate trips
    public ResponseEntity<TripBooking> rateTrip(@PathVariable Integer tripId, @Valid @RequestBody RatingRequest ratingRequest) {
        // Here you would add an extra security check to ensure that the logged-in customer
        // is the one who actually owns this trip. For now, this is a good start.
        TripBooking ratedTrip = tripBookingService.rateTrip(tripId, ratingRequest);
        return ResponseEntity.ok(ratedTrip);
    }
}
