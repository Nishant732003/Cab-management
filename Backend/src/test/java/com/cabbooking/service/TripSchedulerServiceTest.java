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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TripSchedulerService.
 *
 * Focuses on assigning drivers to scheduled trips that are due.
 * Scenarios covered:
 * - Scheduled trips with available drivers
 * - No scheduled trips available
 */
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

    /**
     * Initialize reusable test data:
     * - One available driver and cab
     * - One scheduled trip
     */
    @BeforeEach
    void setUp() {
        // Create an available cab
        availableCab = new Cab();
        availableCab.setCabId(1);
        availableCab.setCarType("Sedan");
        availableCab.setIsAvailable(true);

        // Create an available driver assigned to the cab
        availableDriver = new Driver();
        availableDriver.setId(1);
        availableDriver.setUsername("testdriver");
        availableDriver.setRating(5.0f);
        availableDriver.setCab(availableCab);
        availableDriver.setVerified(true);
        availableDriver.setIsAvailable(true);
        availableDriver.setLatitude(1.0);
        availableDriver.setLongitude(1.0);
        
        // Create a scheduled trip due in 5 minutes
        scheduledTrip = new TripBooking();
        scheduledTrip.setTripBookingId(1);
        scheduledTrip.setStatus(TripStatus.SCHEDULED);
        scheduledTrip.setCarType("Sedan");
        scheduledTrip.setFromDateTime(LocalDateTime.now().plusMinutes(5));
        scheduledTrip.setFromLatitude(1.0001);
        scheduledTrip.setFromLongitude(1.0001);
    }
    
    /**
     * Test scenario:
     * Assign available drivers to scheduled trips that are due.
     *
     * Workflow:
     * 1. Mock all scheduled trips from repository
     * 2. Mock all available drivers
     * 3. Invoke scheduler method
     * 4. Verify trip status is updated to CONFIRMED
     * 5. Verify driver and cab availability is set to false
     * 6. Verify repository save calls
     */
    @Test
    void assignDriversToScheduledTrips_assignsDriverToDueTrip() {
        // Mock repository to return the scheduled trip
        when(tripBookingRepository.findAll()).thenReturn(Arrays.asList(scheduledTrip));

        // Mock repository to return the available driver
        when(driverRepository.findAll()).thenReturn(Arrays.asList(availableDriver));

        // Invoke the method under test
        tripSchedulerService.assignDriversToScheduledTrips();

        // Verify that driver repository save was called to update availability
        verify(driverRepository, times(1)).save(availableDriver);

        // Verify that trip repository save was called to update trip status
        verify(tripBookingRepository, times(1)).save(scheduledTrip);

        // Assertions to confirm state changes
        assertEquals(TripStatus.CONFIRMED, scheduledTrip.getStatus());
        assertFalse(availableDriver.getIsAvailable());
        assertFalse(availableCab.getIsAvailable());
    }

    /**
     * Test scenario:
     * No scheduled trips are due; scheduler should do nothing.
     *
     * Workflow:
     * 1. Mock repository to return empty list
     * 2. Invoke scheduler method
     * 3. Verify that no repository save calls occurred
     */
    @Test
    void assignDriversToScheduledTrips_noDueTrips_doesNothing() {
        // Mock repository to return empty list (no scheduled trips)
        when(tripBookingRepository.findAll()).thenReturn(Collections.emptyList());

        // Invoke the method under test
        tripSchedulerService.assignDriversToScheduledTrips();

        // Verify that driver repository save was never called
        verify(driverRepository, never()).save(any(Driver.class));

        // Verify that trip repository save was never called
        verify(tripBookingRepository, never()).save(any(TripBooking.class));
    }
}
