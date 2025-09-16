package com.cabbooking.service;

import com.cabbooking.model.Driver;
import com.cabbooking.repository.DriverRepository;
import com.cabbooking.repository.TripBookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DriverServiceImpl.
 *
 * Tests cover key functionalities related to driver management:
 * - Retrieving top-rated drivers
 * - Retrieving unverified drivers
 * - Verifying drivers
 * - Uploading and removing driver profile photos
 *
 * Dependencies are mocked to isolate the service logic:
 * - DriverRepository: Simulates database operations for drivers
 * - TripBookingRepository: Simulated, though not directly tested here
 * - IFileUploadService: Simulates file upload and deletion operations
 */
@ExtendWith(MockitoExtension.class)
public class DriverServiceImplTest {

    // Mocked repository to simulate database interactions for Driver entities
    @Mock
    private DriverRepository driverRepository;

    // Mocked service to simulate file upload/delete functionality
    @Mock
    private IFileUploadService fileUploadService;

    // Mocked repository for trip booking operations (used internally)
    @Mock
    private TripBookingRepository tripBookingRepository;

    // Service under test with injected mocked dependencies
    @InjectMocks
    private DriverServiceImpl driverService;

    // Sample driver object used across multiple tests
    private Driver testDriver;

    /**
     * Sets up common test data before each test.
     * Initializes a Driver object with basic fields like ID, username, availability, and rating.
     */
    @BeforeEach
    void setUp() {
        testDriver = new Driver();
        testDriver.setId(1);
        testDriver.setUsername("testDriver");
        testDriver.setIsAvailable(true);
        testDriver.setRating(4.5f);
    }

    /**
     * Tests that getBestDrivers() correctly filters and returns drivers
     * with a rating above 4.5.
     *
     * Workflow:
     * - Creates an additional driver with average rating (4.5)
     * - Mocks repository to return both drivers
     * - Asserts that only the higher-rated driver is returned
     * - Verifies repository call
     */
    @Test
    void getBestDrivers_returnsFilteredList() {
        Driver averageDriver = new Driver();
        averageDriver.setRating(4.5f);

        when(driverRepository.findAll()).thenReturn(Arrays.asList(testDriver, averageDriver));

        List<Driver> bestDrivers = driverService.getBestDrivers();

        assertEquals(1, bestDrivers.size());
        assertEquals(testDriver.getUsername(), bestDrivers.get(0).getUsername());
        verify(driverRepository, times(1)).findAll();
    }

    /**
     * Tests that getUnverifiedDrivers() returns all drivers who are not verified.
     *
     * Workflow:
     * - Creates an unverified driver
     * - Mocks repository to return the unverified driver
     * - Asserts the driver is returned and is unverified
     * - Verifies repository call
     */
    @Test
    void getUnverifiedDrivers_returnsUnverifiedDrivers() {
        Driver unverifiedDriver = new Driver();
        unverifiedDriver.setVerified(false);
        when(driverRepository.findByVerifiedFalse()).thenReturn(Arrays.asList(unverifiedDriver));

        List<Driver> unverifiedDrivers = driverService.getUnverifiedDrivers();

        assertEquals(1, unverifiedDrivers.size());
        assertFalse(unverifiedDrivers.get(0).getVerified());
        verify(driverRepository, times(1)).findByVerifiedFalse();
    }

    /**
     * Tests that verifyDriver() successfully sets the verified status to true
     * for a valid driver ID.
     *
     * Workflow:
     * - Mocks repository to find the driver by ID
     * - Mocks save() to simulate updating the driver
     * - Asserts the returned message and verified status
     * - Verifies repository interactions
     */
    @Test
    void verifyDriver_validId_updatesVerificationStatus() {
        when(driverRepository.findById(anyInt())).thenReturn(Optional.of(testDriver));
        when(driverRepository.save(any(Driver.class))).thenReturn(testDriver);

        String result = driverService.verifyDriver(1);

        assertEquals("Driver verified successfully", result);
        assertTrue(testDriver.getVerified());
        verify(driverRepository, times(1)).findById(1);
        verify(driverRepository, times(1)).save(testDriver);
    }

    /**
     * Tests that verifyDriver() throws an exception for an invalid driver ID.
     *
     * Workflow:
     * - Mocks repository to return empty when driver is not found
     * - Asserts that IllegalArgumentException is thrown
     * - Verifies save() is never called
     */
    @Test
    void verifyDriver_invalidId_throwsException() {
        when(driverRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> driverService.verifyDriver(99));
        verify(driverRepository, times(1)).findById(99);
        verify(driverRepository, never()).save(any(Driver.class));
    }

    /**
     * Tests that uploadProfilePhoto() successfully uploads a file
     * and updates the driver's profile photo URL.
     *
     * Workflow:
     * - Mocks repository to find the driver by username
     * - Mocks fileUploadService to simulate file upload
     * - Mocks save() to return the updated driver
     * - Asserts profile photo URL is correctly set
     * - Verifies repository save call
     */
    @Test
    void uploadProfilePhoto_validFile_returnsUpdatedDriver() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(driverRepository.findByUsername(anyString())).thenReturn(testDriver);
        when(fileUploadService.uploadFile(any(MultipartFile.class))).thenReturn("new-photo.jpg");
        when(driverRepository.save(any(Driver.class))).thenReturn(testDriver);

        Driver updatedDriver = driverService.uploadProfilePhoto("testDriver", mockFile);

        assertNotNull(updatedDriver);
        assertNotNull(updatedDriver.getProfilePhotoUrl());
        assertEquals("/api/files/new-photo.jpg", updatedDriver.getProfilePhotoUrl());
        verify(driverRepository, times(1)).save(testDriver);
    }

    /**
     * Tests that removeProfilePhoto() deletes an existing photo
     * and sets the driver's profile photo URL to null.
     *
     * Workflow:
     * - Sets an existing profile photo URL on testDriver
     * - Mocks repository to return the driver
     * - Mocks save() to return updated driver
     * - Asserts profile photo URL is null after removal
     * - Verifies file deletion and repository save calls
     */
    @Test
    void removeProfilePhoto_withExistingPhoto_removesPhoto() throws IOException {
        testDriver.setProfilePhotoUrl("/api/files/old-photo.jpg");
        when(driverRepository.findByUsername(anyString())).thenReturn(testDriver);
        when(driverRepository.save(any(Driver.class))).thenReturn(testDriver);

        Driver updatedDriver = driverService.removeProfilePhoto("testDriver");

        assertNotNull(updatedDriver);
        assertNull(updatedDriver.getProfilePhotoUrl());
        verify(fileUploadService, times(1)).deleteFile("old-photo.jpg");
        verify(driverRepository, times(1)).save(testDriver);
    }
}
