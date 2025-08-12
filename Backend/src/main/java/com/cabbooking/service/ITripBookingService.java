package com.cabbooking.service;

import com.cabbooking.dto.TripBookingRequest;
import com.cabbooking.model.TripBooking;

import java.util.List;

public interface ITripBookingService {
    TripBooking bookTrip(TripBookingRequest tripBookingRequest);
    TripBooking updateTripStatus(Integer tripId, String status);
    TripBooking completeTrip(Integer tripId);
    List<TripBooking> viewAllTripsCustomer(Integer customerId);
}
