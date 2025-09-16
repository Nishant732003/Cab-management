package com.cabbooking.service;

import com.cabbooking.dto.DriverRegistrationRequest;
import com.cabbooking.model.Cab;
import com.cabbooking.model.Driver;
import com.cabbooking.repository.CabRepository;
import com.cabbooking.repository.DriverRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DriverRegistrationServiceImpl.
 *
 * Tests cover driver registration scenarios including:
 * - Successful registration with a new username/email
 * - Registration with duplicate username
 * - Registration with duplicate email
 *
 * Dependencies:
 * - DriverRepository: Mocked to simulate database operations
 * - CabRepository: Mocked to simulate saving associated cab entity
 * - PasswordEncoder: Mocked to simulate password encryption
 */
@ExtendWith(MockitoExtension.class)
public class DriverRegistrationServiceImplTest {

    // Mocked repository to simulate database operations for Driver entities
    @Mock
    private DriverRepository driverRepository;

    // Mocked repository to simulate database operations for Cab entities
    @Mock
    private CabRepository cabRepository;

    // Mocked password encoder to simulate password hashing
    @Mock
    private PasswordEncoder passwordEncoder;

    // Service under test with mocked dependencies injected
    @InjectMocks
    private DriverRegistrationServiceImpl driverRegistrationService;

    // Test data objects for registration request, Driver, and Cab
    private DriverRegistrationRequest testRequest;
    private Driver testDriver;
    private Cab testCab;

    /**
     * Sets up test data before each test method runs.
     *
     * Workflow:
     * - Creates a sample DriverRegistrationRequest
     * - Creates sample Driver and Cab objects
     */
    @BeforeEach
    void setUp() {
        testRequest = new DriverRegistrationRequest();
        testRequest.setUsername("testdriver");
        testRequest.setEmail("driver@test.com");
        testRequest.setPassword("password123");
        testRequest.setLicenceNo("LIC123");
        testRequest.setFirstName("Test");
        testRequest.setLastName("Driver");

        testDriver = new Driver();
        testDriver.setUsername("testdriver");
        testDriver.setEmail("driver@test.com");

        testCab = new Cab();
        testCab.setCarType("Sedan");
    }

    /**
     * Tests successful registration of a new driver.
     *
     * Workflow:
     * - Mocks repository to indicate username/email are not taken
     * - Mocks password encoder to return encoded password
     * - Mocks repository save() methods to return Driver and Cab objects
     * - Verifies that both Driver and Cab are saved exactly once
     */
    @Test
    void registerDriver_validRequest_savesNewDriver() {
        when(driverRepository.existsByUsername(anyString())).thenReturn(false);
        when(driverRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(driverRepository.save(any(Driver.class))).thenReturn(testDriver);
        when(cabRepository.save(any(Cab.class))).thenReturn(testCab);

        driverRegistrationService.registerDriver(testRequest);

        verify(driverRepository, times(1)).save(any(Driver.class));
        verify(cabRepository, times(1)).save(any(Cab.class));
    }

    /**
     * Tests registration failure due to duplicate username.
     *
     * Workflow:
     * - Mocks repository to indicate username already exists
     * - Asserts that an IllegalArgumentException is thrown
     * - Verifies that neither Driver nor Cab is saved
     */
    @Test
    void registerDriver_duplicateUsername_throwsException() {
        when(driverRepository.existsByUsername("testdriver")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> driverRegistrationService.registerDriver(testRequest));
        verify(driverRepository, never()).save(any(Driver.class));
        verify(cabRepository, never()).save(any(Cab.class));
    }

    /**
     * Tests registration failure due to duplicate email.
     *
     * Workflow:
     * - Mocks repository to indicate email already exists while username is new
     * - Asserts that an IllegalArgumentException is thrown
     * - Verifies that neither Driver nor Cab is saved
     */
    @Test
    void registerDriver_duplicateEmail_throwsException() {
        when(driverRepository.existsByUsername("testdriver")).thenReturn(false);
        when(driverRepository.existsByEmail("driver@test.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> driverRegistrationService.registerDriver(testRequest));
        verify(driverRepository, never()).save(any(Driver.class));
        verify(cabRepository, never()).save(any(Cab.class));
    }
}
