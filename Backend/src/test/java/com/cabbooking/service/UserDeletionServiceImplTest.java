package com.cabbooking.service;

import com.cabbooking.model.AbstractUser;
import com.cabbooking.model.Admin;
import com.cabbooking.model.Customer;
import com.cabbooking.model.Driver;
import com.cabbooking.repository.AdminRepository;
import com.cabbooking.repository.CustomerRepository;
import com.cabbooking.repository.DriverRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserDeletionServiceImplTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private DriverRepository driverRepository;
    
    @Mock
    private IFileUploadService fileUploadService;

    @InjectMocks
    private UserDeletionServiceImpl userDeletionService;

    private Driver testDriver;
    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testDriver = new Driver();
        testDriver.setUsername("testdriver");
        testDriver.setProfilePhotoUrl("/api/files/driver-photo.jpg");

        testCustomer = new Customer();
        testCustomer.setUsername("testcustomer");
    }

    @Test
    void deleteUser_deletesDriverAndPhoto() throws IOException {
        when(driverRepository.findByUsername("testdriver")).thenReturn(testDriver);
        
        userDeletionService.deleteUser("testdriver");

        verify(driverRepository, times(1)).deleteByUsername("testdriver");
        verify(fileUploadService, times(1)).deleteFile("driver-photo.jpg");
    }

    @Test
    void deleteUser_deletesCustomerWithoutPhoto() throws IOException {
        when(driverRepository.findByUsername("testcustomer")).thenReturn(null);
        when(customerRepository.findByUsername("testcustomer")).thenReturn(testCustomer);

        userDeletionService.deleteUser("testcustomer");

        verify(customerRepository, times(1)).deleteByUsername("testcustomer");
        verify(fileUploadService, never()).deleteFile(anyString());
    }

    @Test
    void deleteUser_userNotFound_throwsException() throws IOException {
        when(driverRepository.findByUsername("nonexistent")).thenReturn(null);
        when(customerRepository.findByUsername("nonexistent")).thenReturn(null);
        when(adminRepository.findByUsername("nonexistent")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> userDeletionService.deleteUser("nonexistent"));
        verify(driverRepository, never()).deleteByUsername(anyString());
        verify(customerRepository, never()).deleteByUsername(anyString());
        verify(adminRepository, never()).deleteByUsername(anyString());
    }
}