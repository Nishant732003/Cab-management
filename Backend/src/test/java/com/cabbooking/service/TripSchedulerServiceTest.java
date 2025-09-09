package com.cabbooking.service;

import com.cabbooking.model.Cab;
import com.cabbooking.model.Driver;
import com.cabbooking.model.TripBooking;
import com.cabbooking.model.TripStatus;
import com.cabbooking.repository.DriverRepository;
import com.cabbooking.repository.TripBookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.annotation.Scheduled;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TripSchedulerServiceTest {

    @Mock
    private TripBookingRepository tripBookingRepository;

    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private TripSchedulerService tripSchedulerService;

    private TripBooking scheduledTrip;
    private Driver availableDriver;
    private Cab availableCab;

    @BeforeEach
    void setUp() {
        availableCab = new Cab();
        availableCab.setCabId(1);
        availableCab.setCarType("Sedan");
        availableCab.setIsAvailable(true);

        availableDriver = new Driver();
        availableDriver.setId(1);
        availableDriver.setUsername("testdriver");
        availableDriver.setRating(5.0f);
        availableDriver.setCab(availableCab);
        availableDriver.setVerified(true);
        availableDriver.setIsAvailable(true);
        availableDriver.setLatitude(1.0);
        availableDriver.setLongitude(1.0);
        
        scheduledTrip = new TripBooking();
        scheduledTrip.setTripBookingId(1);
        scheduledTrip.setStatus(TripStatus.SCHEDULED);
        scheduledTrip.setCarType("Sedan");
        scheduledTrip.setFromDateTime(LocalDateTime.now().plusMinutes(5));
        scheduledTrip.setFromLatitude(1.0001);
        scheduledTrip.setFromLongitude(1.0001);
    }
    
    @Test
    void assignDriversToScheduledTrips_assignsDriverToDueTrip() {
        when(tripBookingRepository.findAll()).thenReturn(Arrays.asList(scheduledTrip));
        when(driverRepository.findAll()).thenReturn(Arrays.asList(availableDriver));

        tripSchedulerService.assignDriversToScheduledTrips();

        verify(driverRepository, times(1)).save(availableDriver);
        verify(tripBookingRepository, times(1)).save(scheduledTrip);
        assertEquals(TripStatus.CONFIRMED, scheduledTrip.getStatus());
        assertFalse(availableDriver.getIsAvailable());
        assertFalse(availableCab.getIsAvailable());
    }

    @Test
    void assignDriversToScheduledTrips_noDueTrips_doesNothing() {
        when(tripBookingRepository.findAll()).thenReturn(Collections.emptyList());

        tripSchedulerService.assignDriversToScheduledTrips();

        verify(driverRepository, never()).save(any(Driver.class));
        verify(tripBookingRepository, never()).save(any(TripBooking.class));
    }
}