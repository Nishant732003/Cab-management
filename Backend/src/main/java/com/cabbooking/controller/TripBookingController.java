package com.cabbooking.controller;

import com.cabbooking.dto.RatingRequest;
import com.cabbooking.dto.TripBookingRequest;
import com.cabbooking.model.TripBooking;
import com.cabbooking.service.ITripBookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
