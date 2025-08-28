package com.cabbooking.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cabbooking.dto.RatingRequest;
import com.cabbooking.dto.TripBookingRequest;
import com.cabbooking.exception.AuthenticationException;
import com.cabbooking.model.Cab;
import com.cabbooking.model.Customer;
import com.cabbooking.model.Driver;
import com.cabbooking.model.TripBooking;
import com.cabbooking.model.TripStatus;
import com.cabbooking.repository.CabRepository;
import com.cabbooking.repository.CustomerRepository;
import com.cabbooking.repository.DriverRepository;
import com.cabbooking.repository.TripBookingRepository;

/**
 * Implementation of the {@link ITripBookingService} interface. This class
 * contains the core business logic for managing trip bookings. It coordinates
 * between different repositories to create and manage trip data.
 *
 * Main Responsibilities: - Handles the logic for booking a new trip. - Updates
 * the status of a trip. - Marks a trip as completed and calculates the final
 * bill. - Retrieves all trips for a specific customer. - Handles security using
 * method-level security.
 *
 * Security: - All endpoints are secured using method-level security. - Only
 * users with the 'Customer' role can access these endpoints. - Users can only
 * update their own trip status. - Admins can update any user's trip status. -
 * Drivers can only update their own trip status.
 */
@Service
public class TripBookingServiceImpl implements ITripBookingService {

    /*
     * Repository for TripBooking entity.
     * Provides CRUD operations for TripBooking entity.
     */
    @Autowired
    private TripBookingRepository tripBookingRepository;

    /*
     * Repository for Customer entity.
     * Provides CRUD operations for Customer entity.
     */
    @Autowired
    private CustomerRepository customerRepository;

    /*
     * Repository for Driver entity.
     * Provides CRUD operations for Driver entity.
     */
    @Autowired
    private DriverRepository driverRepository;

    /*
     * Repository for Cab entity.
     * Provides CRUD operations for Cab entity.
     */
    @Autowired
    private CabRepository cabRepository;

    /**
     * Handles the logic for booking a new trip. This method now supports both immediate and scheduled bookings.
     *
     * Workflow: 
     * - Validate that the customer exists. 
     * - Check if a future scheduledTime is provided in the request. 
     * - Set the status to SCHEDULED if a future scheduledTime is provided in the request. 
     * - Else find the best available driver (highest rating). 
     * - Create a new TripBooking entity. 
     * - Assign the driver to the trip. 
     * - Save the trip to the database. 
     * - Return the saved trip.
     *
     * @param tripBookingRequest The request from the customer containing trip
     * details.
     * @return The saved TripBooking object.
     * @throws AuthenticationException if the customer ID is invalid.
     * @throws RuntimeException if no drivers are available for an immediate booking.
     */
    @Override
    @Transactional
    public TripBooking bookTrip(TripBookingRequest tripBookingRequest) {
        // Validate that the customer exists. This is common for both booking types.
        Customer customer = customerRepository.findById(tripBookingRequest.getCustomerId())
                .orElseThrow(() -> new AuthenticationException("Customer not found..."));

        // Check if a future scheduledTime is provided in the request.
        if (tripBookingRequest.getScheduledTime() != null && tripBookingRequest.getScheduledTime().isAfter(LocalDateTime.now())) {
            // Create a new TripBooking entity.
            TripBooking scheduledTrip = new TripBooking();
            scheduledTrip.setCustomer(customer);
            scheduledTrip.setFromLocation(tripBookingRequest.getFromLocation());
            scheduledTrip.setToLocation(tripBookingRequest.getToLocation());
            scheduledTrip.setDistanceInKm(tripBookingRequest.getDistanceInKm());

            // Set the car type to fetch a driver associated with that car type
            scheduledTrip.setCarType(tripBookingRequest.getCarType());

            // Set the status to SCHEDULED. No driver or cab is assigned at this point.
            scheduledTrip.setStatus(TripStatus.SCHEDULED);

            // The start time of the trip is the future time provided by the user.
            scheduledTrip.setFromDateTime(tripBookingRequest.getScheduledTime());

            // Save the scheduled trip to the database. The background scheduler will handle the rest.
            return tripBookingRepository.save(scheduledTrip);

        } else {
            // Find the best available driver WITH the requested car type
            Driver bestAvailableDriver = driverRepository.findAll().stream()
                    .filter(driver -> 
                        driver.getVerified() &&
                        driver.getIsAvailable() &&
                        driver.getCab() != null && // Ensure driver has a cab
                        driver.getCab().getCarType().equalsIgnoreCase(tripBookingRequest.getCarType()) // Match the car type
                    )
                    .max(Comparator.comparing(Driver::getRating))
                    .orElseThrow(() -> new RuntimeException("No '" + tripBookingRequest.getCarType() + "' drivers are available at the moment."));

            Cab assignedCab = bestAvailableDriver.getCab();

            // Mark the driver and their cab as unavailable
            bestAvailableDriver.setIsAvailable(false);
            assignedCab.setIsAvailable(false);
            driverRepository.save(bestAvailableDriver);

            // Create and save the trip
            TripBooking newTrip = new TripBooking();
            newTrip.setCustomer(customer);
            newTrip.setDriver(bestAvailableDriver);
            newTrip.setCab(assignedCab);
            newTrip.setFromLocation(tripBookingRequest.getFromLocation());
            newTrip.setToLocation(tripBookingRequest.getToLocation());
            newTrip.setDistanceInKm(tripBookingRequest.getDistanceInKm());
            newTrip.setCarType(tripBookingRequest.getCarType());
            newTrip.setStatus(TripStatus.CONFIRMED);
            newTrip.setFromDateTime(LocalDateTime.now());

            return tripBookingRepository.save(newTrip);
        }
    }

    /**
     * Updates the status of a trip according to predefined business rules.
     *
     * Main Responsibilities:
     * - Handles the logic for updating the status of a trip.
     * - Applies business rules for status transitions.
     * - Throws an exception if the status transition is not allowed.
     *
     * @param tripId The ID of the trip.
     * @param newStatusStr The new status as a string (e.g., "IN_PROGRESS", "CANCELLED").
     * @return The updated trip.
     * @throws IllegalStateException if the status transition is not allowed.
     */
    @Override
    @Transactional
    public TripBooking updateTripStatus(Integer tripId, String newStatusStr) {
        // Find the trip or throw an exception if it doesn't exist.
        TripBooking trip = tripBookingRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found with ID: " + tripId));

        TripStatus newStatus = TripStatus.valueOf(newStatusStr.toUpperCase());
        TripStatus currentStatus = trip.getStatus();

        // Apply the business rules for status transitions.
        switch (currentStatus) {
            case SCHEDULED -> {
                if (newStatus == TripStatus.CANCELLED) {
                    trip.setStatus(newStatus);
                } else {
                    throw new IllegalStateException("A scheduled trip can only be CANCELLED.");
                }
            }

            case CONFIRMED -> {
                if (newStatus == TripStatus.IN_PROGRESS || newStatus == TripStatus.CANCELLED) {
                    trip.setStatus(newStatus);
                } else {
                    throw new IllegalStateException("A confirmed trip can only be moved to IN_PROGRESS or CANCELLED.");
                }
            }

            case IN_PROGRESS -> {
                if (newStatus == TripStatus.CANCELLED) {
                    trip.setStatus(newStatus);
                } else {
                    throw new IllegalStateException("An in-progress trip can only be CANCELLED.");
                }
            }

            case COMPLETED, CANCELLED -> // If the trip is already completed or cancelled, no further status changes are allowed.
                throw new IllegalStateException("A " + currentStatus.toString().toLowerCase() + " trip cannot be changed.");
        }
        // A trip that is scheduled but not yet confirmed can only be cancelled.

        // If the trip is cancelled, make the assigned driver and cab available again.
        // This check is safe for scheduled trips because their driver and cab will be null.
        if (newStatus == TripStatus.CANCELLED) {
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
        }

        return tripBookingRepository.save(trip);
    }

    /**
     * Completes a trip, calculates the bill, and sets the end time.
     *
     * Workflow:
     * - Completes the trip.
     * - Calculates the final bill.
     * - Sets the end time.
     * - Sets the driver and cab to available.
     * - Returns the completed trip.
     *
     * @param tripId The ID of the trip to complete.
     * @return The completed trip with the final bill.
     */
    @Override
    @Transactional
    public TripBooking completeTrip(Integer tripId) {
        TripBooking trip = tripBookingRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found..."));

        // Complete the trip
        trip.setToDateTime(LocalDateTime.now());
        trip.setStatus(TripStatus.COMPLETED);

        // Calculate the final bill
        float bill = trip.getDistanceInKm() * trip.getCab().getPerKmRate();
        trip.setBill(bill);

        // Set the driver and cab to available
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
     * Workflow:
     * - Retrieves all trips for the customer.
     * - Returns the list of trips.
     *
     * @param customerId The customer's ID.
     * @return A list of trips.
     */
    @Override
    public List<TripBooking> getAllTripsCustomer(Integer customerId) {
        return tripBookingRepository.findByCustomer_Id(customerId);
    }

    /**
     * Applies a customer's rating to a completed trip and updates the driver's average rating.
     *
     * Workflow:
     * - Finds the trip.
     * - Validates the request.
     * - Gets the driver and the new rating.
     * - Calculates the new average rating.
     * - Updates the trip with the customer's rating.
     * - Returns the updated trip.
     *
     * @param tripId The ID of the trip to rate.
     * @param ratingRequest The DTO containing the rating value.
     * @return The updated trip with the customer's rating recorded.
     */
    @Override
    @Transactional
    public TripBooking rateTrip(Integer tripId, RatingRequest ratingRequest) {
        // Find the trip
        TripBooking trip = tripBookingRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found with ID: " + tripId));

        // Validate the request
        if (trip.getStatus() != TripStatus.COMPLETED) {
            throw new IllegalStateException("Trip must be completed before it can be rated.");
        }
        if (trip.getCustomerRating() != null) {
            throw new IllegalStateException("This trip has already been rated.");
        }

        // Get the driver and the new rating
        Driver driver = trip.getDriver();
        Integer newRating = ratingRequest.getRating();

        // Calculate the new average rating
        float currentTotalPoints = driver.getRating() * driver.getTotalRatings();
        int newTotalRatings = driver.getTotalRatings() + 1;
        float newAverageRating = (currentTotalPoints + newRating) / newTotalRatings;

        // Update and save the driver
        driver.setRating(newAverageRating);
        driver.setTotalRatings(newTotalRatings);
        driverRepository.save(driver);

        // Update and save the trip with the customer's rating
        trip.setCustomerRating(newRating);
        return tripBookingRepository.save(trip);
    }
}
