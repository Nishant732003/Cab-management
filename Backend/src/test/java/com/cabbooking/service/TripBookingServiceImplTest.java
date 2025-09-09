package com.cabbooking.service;

import com.cabbooking.dto.RatingRequest;
import com.cabbooking.dto.TripBookingRequest;
import com.cabbooking.model.Cab;
import com.cabbooking.model.Customer;
import com.cabbooking.model.Driver;
import com.cabbooking.model.TripBooking;
import com.cabbooking.model.TripStatus;
import com.cabbooking.repository.CabRepository;
import com.cabbooking.repository.CustomerRepository;
import com.cabbooking.repository.DriverRepository;
import com.cabbooking.repository.TripBookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TripBookingServiceImplTest {

    @Mock
    private TripBookingRepository tripBookingRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private DriverRepository driverRepository;
    @Mock
    private CabRepository cabRepository;

    @InjectMocks
    private TripBookingServiceImpl tripBookingService;

    private Customer testCustomer;
    private Driver testDriver;
    private Cab testCab;
    private TripBooking testTrip;
    private TripBookingRequest testRequest;
    private RatingRequest ratingRequest;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setId(1);
        testCustomer.setUsername("customer");

        testCab = new Cab();
        testCab.setCabId(1);
        testCab.setCarType("Sedan");
        testCab.setPerKmRate(10.0f);
        testCab.setIsAvailable(true);

        testDriver = new Driver();
        testDriver.setId(1);
        testDriver.setUsername("driver");
        testDriver.setVerified(true);
        testDriver.setIsAvailable(true);
        testDriver.setCab(testCab);
        testDriver.setLatitude(1.0);
        testDriver.setLongitude(1.0);
        testDriver.setRating(4.5f);
        testDriver.setTotalRatings(10);

        testTrip = new TripBooking();
        testTrip.setTripBookingId(1);
        testTrip.setCustomer(testCustomer);
        testTrip.setDriver(testDriver);
        testTrip.setCab(testCab);
        testTrip.setStatus(TripStatus.CONFIRMED);
        testTrip.setFromDateTime(LocalDateTime.now());
        testTrip.setDistanceInKm(10.0f);

        testRequest = new TripBookingRequest();
        testRequest.setCustomerId(1);
        testRequest.setFromLocation("A");
        testRequest.setToLocation("B");
        testRequest.setDistanceInKm(10.0f);
        testRequest.setCarType("Sedan");
        testRequest.setFromLatitude(1.0);
        testRequest.setFromLongitude(1.0);

        ratingRequest = new RatingRequest();
        ratingRequest.setRating(5);
    }
    
    @Test
    void bookTrip_immediateBooking_findsAndAssignsDriver() {
        when(customerRepository.findById(1)).thenReturn(Optional.of(testCustomer));
        when(driverRepository.findAll()).thenReturn(Collections.singletonList(testDriver));
        when(tripBookingRepository.save(any(TripBooking.class))).thenReturn(testTrip);

        TripBooking bookedTrip = tripBookingService.bookTrip(testRequest);

        assertNotNull(bookedTrip);
        assertEquals(TripStatus.CONFIRMED, bookedTrip.getStatus());
        assertEquals(testDriver, bookedTrip.getDriver());
        assertFalse(testDriver.getIsAvailable());
        assertFalse(testCab.getIsAvailable());
        verify(driverRepository, times(1)).save(testDriver);
        verify(tripBookingRepository, times(1)).save(any(TripBooking.class));
    }

    @Test
    void bookTrip_scheduledBooking_setsStatusToScheduled() {
        testRequest.setScheduledTime(LocalDateTime.now().plusHours(1));
        when(customerRepository.findById(1)).thenReturn(Optional.of(testCustomer));
        when(tripBookingRepository.save(any(TripBooking.class))).thenReturn(testTrip);

        TripBooking scheduledTrip = tripBookingService.bookTrip(testRequest);

        assertNotNull(scheduledTrip);
        assertEquals(TripStatus.SCHEDULED, scheduledTrip.getStatus());
        verify(driverRepository, never()).findAll();
        verify(tripBookingRepository, times(1)).save(any(TripBooking.class));
    }

    @Test
    void updateTripStatus_confirmedToInProgress_succeeds() {
        testTrip.setStatus(TripStatus.CONFIRMED);
        when(tripBookingRepository.findById(1)).thenReturn(Optional.of(testTrip));
        when(tripBookingRepository.save(any(TripBooking.class))).thenReturn(testTrip);
        when(driverRepository.findByUsername("driver")).thenReturn(testDriver);

        TripBooking updatedTrip = tripBookingService.updateTripStatus(1, "IN_PROGRESS", "driver");

        assertNotNull(updatedTrip);
        assertEquals(TripStatus.IN_PROGRESS, updatedTrip.getStatus());
    }

    @Test
    void updateTripStatus_inProgressToCancelled_succeedsAndReleasesResources() {
        testTrip.setStatus(TripStatus.IN_PROGRESS);
        testDriver.setIsAvailable(false);
        testCab.setIsAvailable(false);
        when(tripBookingRepository.findById(1)).thenReturn(Optional.of(testTrip));
        when(tripBookingRepository.save(any(TripBooking.class))).thenReturn(testTrip);
        when(driverRepository.findByUsername("driver")).thenReturn(testDriver);
        when(driverRepository.save(any(Driver.class))).thenReturn(testDriver);
        when(cabRepository.save(any(Cab.class))).thenReturn(testCab);

        TripBooking updatedTrip = tripBookingService.updateTripStatus(1, "CANCELLED", "driver");

        assertNotNull(updatedTrip);
        assertEquals(TripStatus.CANCELLED, updatedTrip.getStatus());
        assertTrue(testDriver.getIsAvailable());
        assertTrue(testCab.getIsAvailable());
        verify(driverRepository, times(1)).save(testDriver);
        verify(cabRepository, times(1)).save(testCab);
    }
    
    @Test
    void updateTripStatus_unauthorizedDriver_throwsAccessDeniedException() {
        testTrip.setStatus(TripStatus.CONFIRMED);
        Driver anotherDriver = new Driver();
        anotherDriver.setUsername("anotherDriver");
        testTrip.setDriver(anotherDriver);
        when(tripBookingRepository.findById(1)).thenReturn(Optional.of(testTrip));

        assertThrows(AccessDeniedException.class, () -> tripBookingService.updateTripStatus(1, "IN_PROGRESS", "driver"));
    }

    @Test
    void completeTrip_validRequest_succeedsAndCalculatesBill() {
        testTrip.setStatus(TripStatus.IN_PROGRESS);
        testDriver.setIsAvailable(false);
        testCab.setIsAvailable(false);
        when(tripBookingRepository.findById(1)).thenReturn(Optional.of(testTrip));
        when(tripBookingRepository.save(any(TripBooking.class))).thenReturn(testTrip);
        when(driverRepository.findByUsername("driver")).thenReturn(testDriver);
        when(driverRepository.save(any(Driver.class))).thenReturn(testDriver);
        when(cabRepository.save(any(Cab.class))).thenReturn(testCab);

        TripBooking completedTrip = tripBookingService.completeTrip(1, "driver");

        assertNotNull(completedTrip);
        assertEquals(TripStatus.COMPLETED, completedTrip.getStatus());
        assertTrue(completedTrip.getBill() > 0);
        assertTrue(testDriver.getIsAvailable());
        assertTrue(testCab.getIsAvailable());
        verify(driverRepository, times(1)).save(testDriver);
        verify(cabRepository, times(1)).save(testCab);
    }
    
    @Test
    void rateTrip_validRequest_updatesTripAndDriverRating() {
        testTrip.setStatus(TripStatus.COMPLETED);
        when(tripBookingRepository.findById(1)).thenReturn(Optional.of(testTrip));
        when(tripBookingRepository.save(any(TripBooking.class))).thenReturn(testTrip);
        when(driverRepository.save(any(Driver.class))).thenReturn(testDriver);
        when(customerRepository.findByUsername("customer")).thenReturn(testCustomer);

        TripBooking ratedTrip = tripBookingService.rateTrip(1, ratingRequest, "customer");

        assertNotNull(ratedTrip);
        assertEquals(5, ratedTrip.getCustomerRating());
        // Verify new driver rating calculation
        verify(driverRepository, times(1)).save(any(Driver.class));
        verify(tripBookingRepository, times(1)).save(any(TripBooking.class));
    }
}