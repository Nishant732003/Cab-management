package com.cabbooking.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.time.LocalDate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cabbooking.dto.RatingRequest;
import com.cabbooking.dto.TripBookingRequest;
import com.cabbooking.dto.TripHistoryResponse;
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

    /*
     * Constant for nearby radius in kilometers.
     */
    private static final double NEARBY_RADIUS_KM = 50000.0;

    /**
     * Handles the logic for booking a new trip. This method now supports both
     * immediate and scheduled bookings.
     *
     * Workflow: - Validate that the customer exists. - Check if a future
     * scheduledTime is provided in the request. - Set the status to SCHEDULED
     * if a future scheduledTime is provided in the request. - Else find the
     * best available driver that is nearby the pickup location (highest
     * rating). - Create a new TripBooking entity. - Assign the driver to the
     * trip. - Save the trip to the database. - Return the saved trip.
     *
     * @param tripBookingRequest The request from the customer containing trip
     * details.
     * @return The saved TripBooking object.
     * @throws AuthenticationException if the customer ID is invalid.
     * @throws RuntimeException if no drivers are available for an immediate
     * booking.
     */
    @Override
    @Transactional
    public TripBooking bookTrip(TripBookingRequest tripBookingRequest) {
        Customer customer = customerRepository.findById(tripBookingRequest.getCustomerId())
                .orElseThrow(() -> new AuthenticationException("Customer not found..."));

        if (tripBookingRequest.getScheduledTime() != null && tripBookingRequest.getScheduledTime().isAfter(LocalDateTime.now())) {
            // --- LOGIC FOR SCHEDULED TRIP ---
            TripBooking scheduledTrip = new TripBooking();
            scheduledTrip.setCustomer(customer);
            scheduledTrip.setFromLocation(tripBookingRequest.getFromLocation());
            scheduledTrip.setToLocation(tripBookingRequest.getToLocation());
            scheduledTrip.setDistanceInKm(tripBookingRequest.getDistanceInKm());
            scheduledTrip.setCarType(tripBookingRequest.getCarType());
            scheduledTrip.setStatus(TripStatus.SCHEDULED);
            scheduledTrip.setFromDateTime(tripBookingRequest.getScheduledTime());

            // Store the starting coordinates for the scheduler to use later
            scheduledTrip.setFromLatitude(tripBookingRequest.getFromLatitude());
            scheduledTrip.setFromLongitude(tripBookingRequest.getFromLongitude());

            return tripBookingRepository.save(scheduledTrip);
        } else {
            // --- LOGIC FOR IMMEDIATE TRIP ---
            // Find all available drivers of the correct car type
            List<Driver> availableDrivers = driverRepository.findAll().stream()
                    .filter(driver
                            -> driver.getVerified()
                    && driver.getIsAvailable()
                    && driver.getCab() != null
                    && driver.getCab().getCarType().equalsIgnoreCase(tripBookingRequest.getCarType())
                    && driver.getLatitude() != null && driver.getLongitude() != null
                    ).toList();

            // Filter for nearby drivers and find the best one by rating
            Driver bestNearbyDriver = availableDrivers.stream()
                    .filter(driver -> calculateDistance(
                    tripBookingRequest.getFromLatitude(),
                    tripBookingRequest.getFromLongitude(),
                    driver.getLatitude(),
                    driver.getLongitude()) <= NEARBY_RADIUS_KM)
                    .max(Comparator.comparing(Driver::getRating))
                    .orElseThrow(() -> new RuntimeException("No '" + tripBookingRequest.getCarType() + "' drivers are available nearby at the moment."));

            // Assign driver and book trip (existing logic)
            Cab assignedCab = bestNearbyDriver.getCab();
            bestNearbyDriver.setIsAvailable(false);
            assignedCab.setIsAvailable(false);
            driverRepository.save(bestNearbyDriver);

            TripBooking newTrip = new TripBooking();
            newTrip.setCustomer(customer);
            newTrip.setDriver(bestNearbyDriver);
            newTrip.setCab(assignedCab);
            newTrip.setFromLocation(tripBookingRequest.getFromLocation());
            newTrip.setToLocation(tripBookingRequest.getToLocation());
            newTrip.setDistanceInKm(tripBookingRequest.getDistanceInKm());
            newTrip.setCarType(tripBookingRequest.getCarType());
            newTrip.setStatus(TripStatus.CONFIRMED);
            newTrip.setFromDateTime(LocalDateTime.now());
            // Store starting coordinates for the trip record
            newTrip.setFromLatitude(tripBookingRequest.getFromLatitude());
            newTrip.setFromLongitude(tripBookingRequest.getFromLongitude());

            return tripBookingRepository.save(newTrip);
        }
    }

    /*
     * Helper method to calculate distance between two coordinates
     */
    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        final int R = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lng2 - lng1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    /**
     * Updates the status of a trip according to predefined business rules.
     *
     * Main Responsibilities: - Handles the logic for updating the status of a
     * trip. - Applies business rules for status transitions. - Throws an
     * exception if the status transition is not allowed.
     *
     * @param tripId The ID of the trip.
     * @param newStatusStr The new status as a string (e.g., "IN_PROGRESS",
     * "CANCELLED").
     * @return The updated trip.
     * @throws IllegalStateException if the status transition is not allowed.
     */
    @Override
    @Transactional
    public TripBooking updateTripStatus(Integer tripId, String newStatusStr, String driverUsername) {
        // Find the trip or throw an exception if it doesn't exist.
        TripBooking trip = tripBookingRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found with ID: " + tripId));

        // Ensure the trip has an assigned driver before checking authorization.
        if (trip.getDriver() == null || !trip.getDriver().getUsername().equals(driverUsername)) {
            throw new AccessDeniedException("You are not authorized to update this trip.");
        }

        // (Existing logic for updating status)
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
    public TripBooking completeTrip(Integer tripId, String driverUsername) {
        TripBooking trip = tripBookingRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found..."));

        if (trip.getDriver() == null || !trip.getDriver().getUsername().equals(driverUsername)) {
            throw new AccessDeniedException("You are not authorized to complete this trip.");
        }

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

    @Override
    public List<TripHistoryResponse> viewAllTripsCustomer(Integer customerId) {
        List<TripBooking> trips = tripBookingRepository.findByCustomer_Id(customerId);
        return trips.stream()
                .map(trip -> new TripHistoryResponse(
                        trip.getTripBookingId(),
                        trip.getFromLocation(),
                        trip.getToLocation(),
                        trip.getFromDateTime(),
                        trip.getToDateTime(),
                        trip.getStatus(),
                        trip.getBill(),
                        trip.getCustomerRating(),
                        trip.getCarType(),
                        trip.getCustomer() != null ? trip.getCustomer().getFirstName() : null,
                        trip.getCustomer() != null ? trip.getCustomer().getLastName() : null,
                        trip.getDriver() != null ? trip.getDriver().getFirstName() : null,
                        trip.getDriver() != null ? trip.getDriver().getLastName() : null
                ))
                .collect(Collectors.toList());
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
    public TripBooking rateTrip(Integer tripId, RatingRequest ratingRequest, String customerUsername) {
        // Find the trip
        TripBooking trip = tripBookingRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found with ID: " + tripId));
        
        if (trip.getCustomer() == null || !trip.getCustomer().getUsername().equals(customerUsername)) {
            throw new AccessDeniedException("You are not authorized to rate this trip.");
        }

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

    // --- ADD THE IMPLEMENTATION FOR THE NEW METHODS ---
    @Override
    public List<TripHistoryResponse> viewAllTripsDriver(Integer driverId) {
        List<TripBooking> trips = tripBookingRepository.findByDriver_Id(driverId);
        return trips.stream()
                .map(trip -> new TripHistoryResponse(
                        trip.getTripBookingId(),
                        trip.getFromLocation(),
                        trip.getToLocation(),
                        trip.getFromDateTime(),
                        trip.getToDateTime(),
                        trip.getStatus(),
                        trip.getBill(),
                        trip.getCustomerRating(),
                        trip.getCarType(),
                        trip.getCustomer() != null ? trip.getCustomer().getFirstName() : null,
                        trip.getCustomer() != null ? trip.getCustomer().getLastName() : null,
                        trip.getDriver() != null ? trip.getDriver().getFirstName() : null,
                        trip.getDriver() != null ? trip.getDriver().getLastName() : null
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<TripBooking> getTripsDatewise(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        return tripBookingRepository.findByFromDateTimeBetween(startOfDay, endOfDay);
    }
    // --- ADD THE MISSING METHOD IMPLEMENTATION BELOW ---
//   @Override
//     public TripDetailsResponse getTripDetails(Integer tripId) {
//     TripBooking trip = tripBookingRepository.findById(tripId)
//         .orElseThrow(() -> new RuntimeException("Trip not found..."));

//     String customerName = trip.getCustomer().getName();
//     String cabType = trip.getCab().getCarType();
//     String numberPlate = trip.getCab().getNumberPlate();

//     return new TripDetailsResponse(customerName, cabType, numberPlate);
// }
    /**
     * Retrieves the most recent trip for a customer to view their bill.
     *
     * @param customerId The ID of the customer.
     * @return The latest TripBooking object for the customer.
     */
    @Override
    public TripBooking viewBill(int customerId) {
        List<TripBooking> trips = tripBookingRepository.findByCustomer_Id(customerId);
        if (!trips.isEmpty()) {
            // Return the most recent trip
            return trips.get(trips.size() - 1);
        }
        return null; // Or throw an exception if no trips are found
    }
}