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

    private static final double NEARBY_RADIUS_KM = 50000.0;

    @Override
    @Transactional
    public TripBooking bookTrip(TripBookingRequest tripBookingRequest) {
        Customer customer = customerRepository.findById(tripBookingRequest.getCustomerId())
                .orElseThrow(() -> new AuthenticationException("Customer not found..."));

        if (tripBookingRequest.getScheduledTime() != null && tripBookingRequest.getScheduledTime().isAfter(LocalDateTime.now())) {
            TripBooking scheduledTrip = new TripBooking();
            scheduledTrip.setCustomer(customer);
            scheduledTrip.setFromLocation(tripBookingRequest.getFromLocation());
            scheduledTrip.setToLocation(tripBookingRequest.getToLocation());
            scheduledTrip.setDistanceInKm(tripBookingRequest.getDistanceInKm());
            scheduledTrip.setCarType(tripBookingRequest.getCarType());
            scheduledTrip.setStatus(TripStatus.SCHEDULED);
            scheduledTrip.setFromDateTime(tripBookingRequest.getScheduledTime());
            scheduledTrip.setFromLatitude(tripBookingRequest.getFromLatitude());
            scheduledTrip.setFromLongitude(tripBookingRequest.getFromLongitude());
            return tripBookingRepository.save(scheduledTrip);
        } else {
            List<Driver> availableDrivers = driverRepository.findAll().stream()
                    .filter(driver
                            -> driver.getVerified()
                    && driver.getIsAvailable()
                    && driver.getCab() != null
                    && driver.getCab().getCarType().equalsIgnoreCase(tripBookingRequest.getCarType())
                    && driver.getLatitude() != null && driver.getLongitude() != null
                    ).toList();

            Driver bestNearbyDriver = availableDrivers.stream()
                    .filter(driver -> calculateDistance(
                    tripBookingRequest.getFromLatitude(),
                    tripBookingRequest.getFromLongitude(),
                    driver.getLatitude(),
                    driver.getLongitude()) <= NEARBY_RADIUS_KM)
                    .max(Comparator.comparing(Driver::getRating))
                    .orElseThrow(() -> new RuntimeException("No '" + tripBookingRequest.getCarType() + "' drivers are available nearby at the moment."));

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
            newTrip.setFromLatitude(tripBookingRequest.getFromLatitude());
            newTrip.setFromLongitude(tripBookingRequest.getFromLongitude());

            return tripBookingRepository.save(newTrip);
        }
    }

    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lng2 - lng1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
@Override
@Transactional
public TripBooking updateTripStatus(Integer tripId, String newStatusStr, String driverUsername) {
    TripBooking trip = tripBookingRepository.findById(tripId)
            .orElseThrow(() -> new RuntimeException("Trip not found with ID: " + tripId));

    if (trip.getDriver() == null || !trip.getDriver().getUsername().equals(driverUsername)) {
        throw new AccessDeniedException("You are not authorized to update this trip.");
    }

    TripStatus newStatus = TripStatus.valueOf(newStatusStr.toUpperCase());
    TripStatus currentStatus = trip.getStatus();

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
            if (newStatus == TripStatus.COMPLETED || newStatus == TripStatus.CANCELLED) {
                trip.setStatus(newStatus);
                if (newStatus == TripStatus.COMPLETED) {
                    trip.setToDateTime(LocalDateTime.now());
                    float bill = trip.getDistanceInKm() * trip.getCab().getPerKmRate();
                    trip.setBill(bill);
                }
            } else {
                throw new IllegalStateException("An in-progress trip can only be COMPLETED or CANCELLED.");
            }
        }
        case COMPLETED, CANCELLED ->
            throw new IllegalStateException("A " + currentStatus.toString().toLowerCase() + " trip cannot be changed.");
    }

    if (newStatus == TripStatus.CANCELLED || newStatus == TripStatus.COMPLETED) {
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
    @Override
    @Transactional
    public TripBooking completeTrip(Integer tripId, String driverUsername) {
        TripBooking trip = tripBookingRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found..."));

        if (trip.getDriver() == null || !trip.getDriver().getUsername().equals(driverUsername)) {
            throw new AccessDeniedException("You are not authorized to complete this trip.");
        }

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
                        trip.getDriver() != null ? trip.getDriver().getLastName() : null,
                        // ✅ Corrected: Add distanceinKm to the constructor call
                        trip.getDistanceInKm(),
                        trip.getDriver() != null ? trip.getDriver().getId() : null,
    trip.getCustomer() != null ? trip.getCustomer().getId() : null,
    trip.getCab() != null ? trip.getCab().getCabId() : null
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TripBooking rateTrip(Integer tripId, RatingRequest ratingRequest, String customerUsername) {
        TripBooking trip = tripBookingRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found with ID: " + tripId));
        
        if (trip.getCustomer() == null || !trip.getCustomer().getUsername().equals(customerUsername)) {
            throw new AccessDeniedException("You are not authorized to rate this trip.");
        }

        if (trip.getStatus() != TripStatus.COMPLETED) {
            throw new IllegalStateException("Trip must be completed before it can be rated.");
        }
        if (trip.getCustomerRating() != null) {
            throw new IllegalStateException("This trip has already been rated.");
        }

        Driver driver = trip.getDriver();
        Integer newRating = ratingRequest.getRating();

        float currentTotalPoints = driver.getRating() * driver.getTotalRatings();
        int newTotalRatings = driver.getTotalRatings() + 1;
        float newAverageRating = (currentTotalPoints + newRating) / newTotalRatings;

        driver.setRating(newAverageRating);
        driver.setTotalRatings(newTotalRatings);
        driverRepository.save(driver);

        trip.setCustomerRating(newRating);
        return tripBookingRepository.save(trip);
    }

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
                        trip.getDriver() != null ? trip.getDriver().getLastName() : null,
                        // ✅ Corrected: Add distanceinKm to the constructor call
                        trip.getDistanceInKm(),
                        trip.getDriver() != null ? trip.getDriver().getId() : null,
    trip.getCustomer() != null ? trip.getCustomer().getId() : null,
    trip.getCab() != null ? trip.getCab().getCabId() : null
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<TripBooking> getTripsDatewise(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        return tripBookingRepository.findByFromDateTimeBetween(startOfDay, endOfDay);
    }

    @Override
    public TripBooking viewBill(int customerId) {
        List<TripBooking> trips = tripBookingRepository.findByCustomer_Id(customerId);
        if (!trips.isEmpty()) {
            return trips.get(trips.size() - 1);
        }
        return null;
    }
}