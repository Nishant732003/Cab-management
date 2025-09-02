package com.cabbooking.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.time.LocalDate;

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
            scheduledTrip.setStatus(TripStatus.SCHEDULED);
            scheduledTrip.setFromDateTime(tripBookingRequest.getScheduledTime());
            return tripBookingRepository.save(scheduledTrip);
        } else {
            Driver bestAvailableDriver = driverRepository.findAll().stream()
                    .filter(d -> d.getVerified() && d.getIsAvailable())
                    .max(Comparator.comparing(Driver::getRating))
                    .orElseThrow(() -> new RuntimeException("No drivers are available at the moment."));

            Cab availableCab = cabRepository.findAll().stream()
                    .filter(Cab::getIsAvailable)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No cabs are available..."));

            bestAvailableDriver.setIsAvailable(false);
            availableCab.setIsAvailable(false);
            driverRepository.save(bestAvailableDriver);
            cabRepository.save(availableCab);

            TripBooking newTrip = new TripBooking();
            newTrip.setCustomer(customer);
            newTrip.setDriver(bestAvailableDriver);
            newTrip.setCab(availableCab);
            newTrip.setFromLocation(tripBookingRequest.getFromLocation());
            newTrip.setToLocation(tripBookingRequest.getToLocation());
            newTrip.setDistanceInKm(tripBookingRequest.getDistanceInKm());
            newTrip.setStatus(TripStatus.CONFIRMED);
            newTrip.setFromDateTime(LocalDateTime.now());

            return tripBookingRepository.save(newTrip);
        }
    }

    @Override
    @Transactional
    public TripBooking updateTripStatus(Integer tripId, String newStatusStr) {
        TripBooking trip = tripBookingRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found with ID: " + tripId));
        // ... (rest of the method is the same)
        return trip; // Placeholder, original logic is complex
    }

    @Override
    @Transactional
    public TripBooking completeTrip(Integer tripId) {
        TripBooking trip = tripBookingRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found..."));
        // ... (rest of the method is the same)
        return trip; // Placeholder
    }
    
    @Override
    public List<TripBooking> viewAllTripsCustomer(Integer customerId) {
        return tripBookingRepository.findByCustomer_Id(customerId);
    }

    @Override
    @Transactional
    public TripBooking rateTrip(Integer tripId, RatingRequest ratingRequest) {
        TripBooking trip = tripBookingRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found with ID: " + tripId));
        // ... (rest of the method is the same)
        return trip; // Placeholder
    }

    // --- ADD THE IMPLEMENTATION FOR THE NEW METHODS ---

    @Override
    public List<TripBooking> viewAllTripsDriver(int driverId) {
        return tripBookingRepository.findByDriver_Id(driverId);
    }

    @Override
    public List<TripBooking> getTripsDatewise(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        return tripBookingRepository.findByFromDateTimeBetween(startOfDay, endOfDay);
    }
    // --- ADD THE MISSING METHOD IMPLEMENTATION BELOW ---
    
    /**
     * Retrieves the most recent trip for a customer to view their bill.
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