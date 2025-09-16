package com.cabbooking.service;

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

/**
 * Unit tests for UserDeletionServiceImpl.
 *
 * Focuses on deleting users (Driver, Customer, Admin) and handling associated resources like profile photos.
 */
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

    /**
     * Initialize reusable test data:
     * - Driver with a profile photo
     * - Customer without a profile photo
     */
    @BeforeEach
    void setUp() {
        testDriver = new Driver();
        testDriver.setUsername("testdriver");
        testDriver.setProfilePhotoUrl("/api/files/driver-photo.jpg");

        testCustomer = new Customer();
        testCustomer.setUsername("testcustomer");
    }

    /**
     * Test scenario:
     * Delete a driver who has a profile photo.
     *
     * Workflow:
     * - Mock the driver repository to return the test driver
     * - Call deleteUser with driver's username
     * - Verify driver repository delete method is called
     * - Verify file upload service deletes the associated photo
     */
    @Test
    void deleteUser_deletesDriverAndPhoto() throws IOException {
        when(driverRepository.findByUsername("testdriver")).thenReturn(testDriver);

        userDeletionService.deleteUser("testdriver");

        // Verify driver deletion
        verify(driverRepository, times(1)).deleteByUsername("testdriver");

        // Verify profile photo deletion
        verify(fileUploadService, times(1)).deleteFile("driver-photo.jpg");
    }

    /**
     * Test scenario:
     * Delete a customer who does not have a profile photo.
     *
     * Workflow:
     * - Mock driver repository to return null (not a driver)
     * - Mock customer repository to return the test customer
     * - Call deleteUser with customer's username
     * - Verify customer repository delete method is called
     * - Verify file upload service is not invoked
     */
    @Test
    void deleteUser_deletesCustomerWithoutPhoto() throws IOException {
        when(driverRepository.findByUsername("testcustomer")).thenReturn(null);
        when(customerRepository.findByUsername("testcustomer")).thenReturn(testCustomer);

        userDeletionService.deleteUser("testcustomer");

        // Verify customer deletion
        verify(customerRepository, times(1)).deleteByUsername("testcustomer");

        // No file deletion should occur
        verify(fileUploadService, never()).deleteFile(anyString());
    }

    /**
     * Test scenario:
     * Attempt to delete a user that does not exist.
     *
     * Workflow:
     * - Mock all repositories to return null for the username
     * - Call deleteUser with nonexistent username
     * - Expect IllegalArgumentException to be thrown
     * - Verify no repository delete methods or file deletion occur
     */
    @Test
    void deleteUser_userNotFound_throwsException() throws IOException {
        when(driverRepository.findByUsername("nonexistent")).thenReturn(null);
        when(customerRepository.findByUsername("nonexistent")).thenReturn(null);
        when(adminRepository.findByUsername("nonexistent")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> userDeletionService.deleteUser("nonexistent"));

        // Verify no deletion methods are invoked
        verify(driverRepository, never()).deleteByUsername(anyString());
        verify(customerRepository, never()).deleteByUsername(anyString());
        verify(adminRepository, never()).deleteByUsername(anyString());
    }
}
