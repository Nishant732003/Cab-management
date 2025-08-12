package com.cabbooking.controller;

import com.cabbooking.dto.TripBookingRequest;
import com.cabbooking.model.TripBooking;
import com.cabbooking.service.ITripBookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
public class TripBookingController {

    @Autowired
    private ITripBookingService tripBookingService;

    @PostMapping("/book")
    public ResponseEntity<TripBooking> bookTrip(@Valid @RequestBody TripBookingRequest tripBookingRequest) {
        TripBooking newTrip = tripBookingService.bookTrip(tripBookingRequest);
        return ResponseEntity.ok(newTrip);
    }

    @PutMapping("/{tripId}/status")
    public ResponseEntity<TripBooking> updateTripStatus(@PathVariable Integer tripId, @RequestParam String status) {
        TripBooking updatedTrip = tripBookingService.updateTripStatus(tripId, status);
        return ResponseEntity.ok(updatedTrip);
    }
    
    @PutMapping("/{tripId}/complete")
    public ResponseEntity<TripBooking> completeTrip(@PathVariable Integer tripId) {
        TripBooking completedTrip = tripBookingService.completeTrip(tripId);
        return ResponseEntity.ok(completedTrip);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<TripBooking>> getCustomerTrips(@PathVariable Integer customerId) {
        List<TripBooking> trips = tripBookingService.viewAllTripsCustomer(customerId);
        return ResponseEntity.ok(trips);
    }
}
