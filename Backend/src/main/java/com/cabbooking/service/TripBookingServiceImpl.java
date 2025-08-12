package com.cabbooking.service;

import com.cabbooking.dto.TripBookingRequest;
import com.cabbooking.exception.AuthenticationException;
import com.cabbooking.model.*;
import com.cabbooking.repository.CabRepository;
import com.cabbooking.repository.CustomerRepository;
import com.cabbooking.repository.DriverRepository;
import com.cabbooking.repository.TripBookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the {@link ITripBookingService} interface.
 * This class contains the core business logic for managing trip bookings.
 * It coordinates between different repositories to create and manage trip data.
 */
@Service
public class TripBookingServiceImpl implements ITripBookingService {

    @Autowired
    private TripBookingRepository tripBookingRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DriverRepository driverRepository;
    
    @Autowired
    private CabRepository cabRepository;

    /**
     * Handles the logic for booking a new trip.
     *
     * @param tripBookingRequest The request from the customer containing trip details.
     * @return The saved {@link TripBooking} object.
     * @throws AuthenticationException if the customer ID is invalid.
     * @throws RuntimeException if no drivers or cabs are available.
     */
    @Override
    public TripBooking bookTrip(TripBookingRequest tripBookingRequest) {
        // 1. Validate the customer
        Customer customer = customerRepository.findById(tripBookingRequest.getCustomerId())
                .orElseThrow(() -> new AuthenticationException("Customer not found with ID: " + tripBookingRequest.getCustomerId()));

        // 2. Find an available driver.
        // This is a basic implementation. A real-world app would have more complex logic,
        // such as checking driver status (e.g., 'AVAILABLE'), location, and rating.
        Driver availableDriver = driverRepository.findAll().stream()
                .filter(Driver::getVerified) // Find a verified driver
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No drivers are available at the moment."));
        
        // 3. Assign a cab.
        // This assumes a driver has one cab. This logic can be expanded later.
        // We are using a placeholder cab for now.
        Cab assignedCab = cabRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("No cabs are available in the system."));

        // 4. Create and save the new trip booking
        TripBooking newTrip = new TripBooking();
        newTrip.setCustomer(customer);
        newTrip.setDriver(availableDriver);
        newTrip.setCab(assignedCab);
        newTrip.setFromLocation(tripBookingRequest.getFromLocation());
        newTrip.setToLocation(tripBookingRequest.getToLocation());
        newTrip.setFromDateTime(LocalDateTime.now());
        newTrip.setStatus(TripStatus.CONFIRMED);
        newTrip.setDistanceInKm(tripBookingRequest.getDistanceInKm());

        return tripBookingRepository.save(newTrip);
    }

    /**
     * Updates the status of a trip.
     *
     * @param tripId The ID of the trip.
     * @param status The new status as a string.
     * @return The updated trip.
     */
    @Override
    public TripBooking updateTripStatus(Integer tripId, String status) {
        TripBooking trip = tripBookingRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found with ID: " + tripId));
        // Convert the string status to the TripStatus enum
        trip.setStatus(TripStatus.valueOf(status.toUpperCase()));
        return tripBookingRepository.save(trip);
    }
    
    /**
     * Completes a trip, calculates the bill, and sets the end time.
     *
     * @param tripId The ID of the trip to complete.
     * @return The completed trip with the final bill.
     */
    @Override
    public TripBooking completeTrip(Integer tripId) {
        TripBooking trip = tripBookingRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found with ID: " + tripId));

        trip.setToDateTime(LocalDateTime.now());
        trip.setStatus(TripStatus.COMPLETED);
        
        // Calculate the bill based on distance and the cab's per-kilometer rate.
        float bill = trip.getDistanceInKm() * trip.getCab().getPerKmRate();
        trip.setBill(bill);

        return tripBookingRepository.save(trip);
    }

    /**
     * Retrieves the trip history for a given customer.
     *
     * @param customerId The customer's ID.
     * @return A list of trips.
     */
    @Override
    public List<TripBooking> viewAllTripsCustomer(Integer customerId) {
        return tripBookingRepository.findByCustomerId(customerId);
    }
}
