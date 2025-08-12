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
    public TripBooking bookTrip(TripBookingRequest tripBookingRequest) {
        Customer customer = customerRepository.findById(tripBookingRequest.getCustomerId())
                .orElseThrow(() -> new AuthenticationException("Customer not found"));

        // Find an available driver (basic logic, can be improved)
        Driver availableDriver = driverRepository.findAll().stream()
                .filter(Driver::getVerified) // Assuming you have a way to check if a driver is available
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No drivers available"));
        
        // For now, let's assume a driver has one cab. This can be more complex.
        Cab assignedCab = cabRepository.findAll().stream().findFirst().orElseThrow(() -> new RuntimeException("No cabs available"));


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

    @Override
    public TripBooking updateTripStatus(Integer tripId, String status) {
        TripBooking trip = tripBookingRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));
        trip.setStatus(TripStatus.valueOf(status.toUpperCase()));
        return tripBookingRepository.save(trip);
    }
    
    @Override
    public TripBooking completeTrip(Integer tripId) {
        TripBooking trip = tripBookingRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        trip.setToDateTime(LocalDateTime.now());
        trip.setStatus(TripStatus.COMPLETED);
        
        // Calculate bill
        float bill = trip.getDistanceInKm() * trip.getCab().getPerKmRate();
        trip.setBill(bill);

        return tripBookingRepository.save(trip);
    }

    @Override
    public List<TripBooking> viewAllTripsCustomer(Integer customerId) {
        return tripBookingRepository.findByCustomerId(customerId);
    }
}
