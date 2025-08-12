package com.cabbooking.service;

import com.cabbooking.dto.RatingRequest;
import com.cabbooking.dto.TripBookingRequest;
import com.cabbooking.exception.AuthenticationException;
import com.cabbooking.model.*;
import com.cabbooking.repository.CustomerRepository;
import com.cabbooking.repository.DriverRepository;
import com.cabbooking.repository.TripBookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cabbooking.repository.CabRepository;
import java.util.Comparator;

import java.time.LocalDateTime;
import java.util.List;

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
    @Transactional
    public TripBooking bookTrip(TripBookingRequest tripBookingRequest) {
        // 1. Validate customer
        Customer customer = customerRepository.findById(tripBookingRequest.getCustomerId())
                .orElseThrow(() -> new AuthenticationException("Customer not found..."));

        // ==> THIS IS THE KEY CHANGE <==
        // 2. Find the best available driver
        Driver bestAvailableDriver = driverRepository.findAll().stream()
                .filter(d -> d.getVerified() && d.getIsAvailable()) // Find verified AND available drivers
                .max(Comparator.comparing(Driver::getRating)) // Find the one with the highest rating
                .orElseThrow(() -> new RuntimeException("No drivers are available at the moment."));

        // 3. Find an available cab
        Cab availableCab = cabRepository.findAll().stream()
                .filter(Cab::getIsAvailable)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No cabs are available..."));

        // 4. Set the driver and cab to unavailable
        bestAvailableDriver.setIsAvailable(false);
        availableCab.setIsAvailable(false);
        driverRepository.save(bestAvailableDriver);
        cabRepository.save(availableCab);

        // 5. Create and save the trip
        TripBooking newTrip = new TripBooking();
        newTrip.setCustomer(customer);
        newTrip.setDriver(bestAvailableDriver);
        newTrip.setCab(availableCab);
        // ... set other trip details from tripBookingRequest
        newTrip.setFromLocation(tripBookingRequest.getFromLocation());
        newTrip.setToLocation(tripBookingRequest.getToLocation());
        newTrip.setDistanceInKm(tripBookingRequest.getDistanceInKm());
        newTrip.setStatus(TripStatus.CONFIRMED);
        newTrip.setFromDateTime(LocalDateTime.now());


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
    @Transactional
    public TripBooking completeTrip(Integer tripId) {
        TripBooking trip = tripBookingRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found..."));

        trip.setToDateTime(LocalDateTime.now());
        trip.setStatus(TripStatus.COMPLETED);
        
        float bill = trip.getDistanceInKm() * trip.getCab().getPerKmRate();
        trip.setBill(bill);
        
        Driver driver = trip.getDriver();
        if (driver != null) {
            driver.setIsAvailable(true);
            driverRepository.save(driver);
        }

        Cab cab = trip.getCab();
        if (cab != null) {
            cab.setIsAvailable(true);
            cabRepository.save(cab);
        }

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
        return tripBookingRepository.findByCustomer_Id(customerId);
    }

    // ==> ADD THE IMPLEMENTATION FOR THE NEW METHOD <==
    @Override
    @Transactional
    public TripBooking rateTrip(Integer tripId, RatingRequest ratingRequest) {
        // 1. Find the trip
        TripBooking trip = tripBookingRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found with ID: " + tripId));
        
        // 2. Validate the request
        if (trip.getStatus() != TripStatus.COMPLETED) {
            throw new IllegalStateException("Trip must be completed before it can be rated.");
        }
        if (trip.getCustomerRating() != null) {
            throw new IllegalStateException("This trip has already been rated.");
        }

        // 3. Get the driver and the new rating
        Driver driver = trip.getDriver();
        Integer newRating = ratingRequest.getRating();
        
        // 4. Calculate the new average rating
        float currentTotalPoints = driver.getRating() * driver.getTotalRatings();
        int newTotalRatings = driver.getTotalRatings() + 1;
        float newAverageRating = (currentTotalPoints + newRating) / newTotalRatings;
        
        // 5. Update and save the driver
        driver.setRating(newAverageRating);
        driver.setTotalRatings(newTotalRatings);
        driverRepository.save(driver);
        
        // 6. Update and save the trip with the customer's rating
        trip.setCustomerRating(newRating);
        return tripBookingRepository.save(trip);
    }
}
