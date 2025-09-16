package com.cabbooking.controller;

import com.cabbooking.dto.FareEstimateResponse;
import com.cabbooking.dto.RatingRequest;
import com.cabbooking.dto.TripBookingRequest;
import com.cabbooking.dto.TripHistoryResponse;
import com.cabbooking.model.TripBooking;
import com.cabbooking.service.ICabService;
import com.cabbooking.service.ITripBookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TripController.
 * Covers booking, updating, completing trips, fare estimation,
 * fetching trip history, and rating trips.
 */
@ExtendWith(MockitoExtension.class)
public class TripControllerTest {

    @Mock
    private ITripBookingService tripBookingService; // Mocked service layer for trips

    @Mock
    private ICabService cabService; // Mocked service layer for cab-related operations

    @InjectMocks
    private TripController tripController; // Controller under test

    private TripBooking testTrip;
    private TripBookingRequest testRequest;
    private Principal principal;
    private TripHistoryResponse historyResponse;

    /**
     * Initialize test objects and mock principal before each test.
     */
    @BeforeEach
    void setUp() {
        // Sample TripBooking object
        testTrip = new TripBooking();
        testTrip.setTripBookingId(1);
        testTrip.setFromLocation("A");
        testTrip.setToLocation("B");
        testTrip.setFromDateTime(LocalDateTime.now());
        testTrip.setDistanceInKm(10.0f);

        // Sample trip history response for driver/customer
        historyResponse = new TripHistoryResponse(
                1, "A", "B", LocalDateTime.now(), null, null, 0.0f,
                null, null, null, null, null, null, 10.0, 1, 1, 1
        );

        // Sample trip booking request
        testRequest = new TripBookingRequest();
        testRequest.setCustomerId(1);
        testRequest.setFromLocation("A");
        testRequest.setToLocation("B");
        testRequest.setDistanceInKm(10.0f);
        testRequest.setCarType("Sedan");
        testRequest.setFromLatitude(12.34);
        testRequest.setFromLongitude(56.78);

        // Simulated authenticated driver principal
        principal = new UsernamePasswordAuthenticationToken("driverUser", "pass");
    }

    /**
     * Test: GET /api/trips/fares
     * Workflow:
     * - Mock cabService to return fare estimates
     * - Call controller method
     * - Assert HTTP 200 OK and correct list size
     */
    @Test
    void getFareEstimates_returnsEstimates() {
        List<FareEstimateResponse> mockEstimates = Collections.singletonList(
                new FareEstimateResponse("Sedan", 150.0f, 150.0f)
        );
        when(cabService.getAllFareEstimates(anyFloat(), anyDouble(), anyDouble())).thenReturn(mockEstimates);

        ResponseEntity<List<FareEstimateResponse>> response =
                tripController.getFareEstimates(10.0f, 12.34, 56.78);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(cabService, times(1)).getAllFareEstimates(10.0f, 12.34, 56.78);
    }

    /**
     * Test: POST /api/trips
     * Workflow:
     * - Mock tripBookingService to return the created trip
     * - Call controller method with a valid request
     * - Assert HTTP 200 OK and response body
     */
    @Test
    void bookTrip_validRequest_returnsNewTrip() {
        when(tripBookingService.bookTrip(any(TripBookingRequest.class))).thenReturn(testTrip);

        ResponseEntity<TripBooking> response = tripController.bookTrip(testRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testTrip, response.getBody());
        verify(tripBookingService, times(1)).bookTrip(testRequest);
    }

    /**
     * Test: PUT /api/trips/{id}/status
     * Workflow:
     * - Mock tripBookingService to return updated trip
     * - Call controller method to update status
     * - Assert HTTP 200 OK and response body
     */
    @Test
    void updateTripStatus_validRequest_returnsUpdatedTrip() {
        when(tripBookingService.updateTripStatus(any(Integer.class), any(String.class), any(String.class)))
                .thenReturn(testTrip);

        ResponseEntity<TripBooking> response = tripController.updateTripStatus(1, "IN_PROGRESS", principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testTrip, response.getBody());
        verify(tripBookingService, times(1))
                .updateTripStatus(1, "IN_PROGRESS", principal.getName());
    }

    /**
     * Test: PUT /api/trips/{id}/complete
     * Workflow:
     * - Mock tripBookingService to return completed trip
     * - Call controller method to complete trip
     * - Assert HTTP 200 OK and response body
     */
    @Test
    void completeTrip_validRequest_returnsCompletedTrip() {
        when(tripBookingService.completeTrip(any(Integer.class), any(String.class))).thenReturn(testTrip);

        ResponseEntity<TripBooking> response = tripController.completeTrip(1, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testTrip, response.getBody());
        verify(tripBookingService, times(1)).completeTrip(1, principal.getName());
    }

    /**
     * Test: GET /api/trips/driver/{driverId}
     * Workflow:
     * - Mock tripBookingService to return trip history list
     * - Call controller method for driver trips
     * - Assert HTTP 200 OK and response body size
     */
    @Test
    void getDriverTrips_validDriverId_returnsTripsList() {
        when(tripBookingService.getTripsByDriver(any(Integer.class)))
                .thenReturn(Collections.singletonList(historyResponse));

        ResponseEntity<List<TripHistoryResponse>> response = tripController.getDriverTrips(1, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(tripBookingService, times(1)).getTripsByDriver(1);
    }

    /**
     * Test: GET /api/trips/customer/{customerId}
     * Workflow:
     * - Mock tripBookingService to return customer trips
     * - Call controller method
     * - Assert HTTP 200 OK and response body size
     */
    @Test
    void getCustomerTrips_validCustomerId_returnsTripsList() {
        when(tripBookingService.getAllTripsCustomer(any(Integer.class)))
                .thenReturn(Collections.singletonList(historyResponse));

        ResponseEntity<List<TripHistoryResponse>> response = tripController.getCustomerTrips(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(tripBookingService, times(1)).getAllTripsCustomer(1);
    }

    /**
     * Test: GET /api/trips/date/{date}
     * Workflow:
     * - Mock tripBookingService to return trips for the date
     * - Call controller method with a valid date
     * - Assert HTTP 200 OK and response body
     */
    @Test
    void getTripsByDate_validDate_returnsTripsList() {
        LocalDate date = LocalDate.now();
        when(tripBookingService.getTripsByDate(any(LocalDate.class)))
                .thenReturn(Collections.singletonList(historyResponse));

        ResponseEntity<List<TripHistoryResponse>> response = tripController.getTripsByDate(date);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(tripBookingService, times(1)).getTripsByDate(date);
    }

    /**
     * Test: POST /api/trips/{id}/rate
     * Workflow:
     * - Prepare rating request
     * - Mock tripBookingService to return updated trip
     * - Call controller method
     * - Assert HTTP 200 OK and response body
     */
    @Test
    void rateTrip_validRequest_returnsRatedTrip() {
        RatingRequest ratingRequest = new RatingRequest();
        ratingRequest.setRating(5);
        when(tripBookingService.rateTrip(any(Integer.class), any(RatingRequest.class), any(String.class)))
                .thenReturn(testTrip);

        ResponseEntity<TripBooking> response = tripController.rateTrip(1, ratingRequest, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testTrip, response.getBody());
        verify(tripBookingService, times(1))
                .rateTrip(1, ratingRequest, principal.getName());
    }

    /**
     * Test: PUT /api/trips/{id}/status
     * Scenario: Unauthorized attempt to update trip status
     * Workflow:
     * - Mock service to throw AccessDeniedException
     * - Call controller method
     * - Assert that exception is thrown
     */
    @Test
    void updateTripStatus_accessDenied_returnsForbidden() {
        doThrow(new AccessDeniedException("Forbidden"))
                .when(tripBookingService).updateTripStatus(any(), any(), any());

        assertThrows(AccessDeniedException.class,
                () -> tripController.updateTripStatus(1, "IN_PROGRESS", principal));
    }

    /**
     * Test: PUT /api/trips/{id}/complete
     * Scenario: Unauthorized attempt to complete a trip
     * Workflow:
     * - Mock service to throw AccessDeniedException
     * - Call controller method
     * - Assert that exception is thrown
     */
    @Test
    void completeTrip_accessDenied_returnsForbidden() {
        doThrow(new AccessDeniedException("Forbidden"))
                .when(tripBookingService).completeTrip(any(), any());

        assertThrows(AccessDeniedException.class,
                () -> tripController.completeTrip(1, principal));
    }
}
