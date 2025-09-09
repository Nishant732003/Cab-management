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

@ExtendWith(MockitoExtension.class)
public class DriverRegistrationServiceImplTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private CabRepository cabRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DriverRegistrationServiceImpl driverRegistrationService;

    private DriverRegistrationRequest testRequest;
    private Driver testDriver;
    private Cab testCab;

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
    
    @Test
    void registerDriver_duplicateUsername_throwsException() {
        when(driverRepository.existsByUsername("testdriver")).thenReturn(true);
        
        assertThrows(IllegalArgumentException.class, () -> driverRegistrationService.registerDriver(testRequest));
        verify(driverRepository, never()).save(any(Driver.class));
        verify(cabRepository, never()).save(any(Cab.class));
    }

    @Test
    void registerDriver_duplicateEmail_throwsException() {
        when(driverRepository.existsByUsername("testdriver")).thenReturn(false);
        when(driverRepository.existsByEmail("driver@test.com")).thenReturn(true);
        
        assertThrows(IllegalArgumentException.class, () -> driverRegistrationService.registerDriver(testRequest));
        verify(driverRepository, never()).save(any(Driver.class));
        verify(cabRepository, never()).save(any(Cab.class));
    }
}