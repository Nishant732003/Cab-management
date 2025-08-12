package com.cabbooking.repository;

import com.cabbooking.model.TripBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TripBookingRepository extends JpaRepository<TripBooking, Integer> {
    List<TripBooking> findByCustomerId(Integer customerId);
}
