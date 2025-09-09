package com.cabbooking.service;

import com.cabbooking.model.Cab;
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

@ExtendWith(MockitoExtension.class)
public class DriverServiceImplTest {

    @Mock
    private DriverRepository driverRepository;
    
    @Mock
    private IFileUploadService fileUploadService;

    @Mock
    private TripBookingRepository tripBookingRepository;

    @InjectMocks
    private DriverServiceImpl driverService;

    private Driver testDriver;

    @BeforeEach
    void setUp() {
        testDriver = new Driver();
        testDriver.setId(1);
        testDriver.setUsername("testDriver");
        testDriver.setIsAvailable(true);
        testDriver.setRating(4.8f);
    }

    @Test
    void getBestDrivers_returnsFilteredList() {
        Driver averageDriver = new Driver();
        averageDriver.setRating(3.5f);
        
        when(driverRepository.findAll()).thenReturn(Arrays.asList(testDriver, averageDriver));
        
        List<Driver> bestDrivers = driverService.getBestDrivers();
        
        assertEquals(1, bestDrivers.size());
        assertEquals(testDriver.getUsername(), bestDrivers.get(0).getUsername());
        verify(driverRepository, times(1)).findAll();
    }
    
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
    
    @Test
    void verifyDriver_invalidId_throwsException() {
        when(driverRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> driverService.verifyDriver(99));
        verify(driverRepository, times(1)).findById(99);
        verify(driverRepository, never()).save(any(Driver.class));
    }
    
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