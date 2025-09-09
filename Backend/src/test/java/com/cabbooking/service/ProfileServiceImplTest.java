package com.cabbooking.service;

import com.cabbooking.dto.UserProfileUpdateRequest;
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
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProfileServiceImplTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private DriverRepository driverRepository;
    
    @InjectMocks
    private ProfileServiceImpl profileService;

    private Customer testCustomer;
    private Driver testDriver;
    private Admin testAdmin;
    private UserProfileUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setId(1);
        testCustomer.setUsername("customeruser");
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");
        testCustomer.setMobileNumber("12345");

        testDriver = new Driver();
        testDriver.setId(2);
        testDriver.setUsername("driveruser");
        testDriver.setLicenceNo("LIC123");
        testDriver.setAddress("Old Address");

        testAdmin = new Admin();
        testAdmin.setId(3);
        testAdmin.setUsername("adminuser");
        testAdmin.setEmail("admin@test.com");

        updateRequest = new UserProfileUpdateRequest();
        updateRequest.setMobileNumber("98765");
        updateRequest.setAddress("New Address");
        updateRequest.setLicenceNo("NEW_LIC");
    }

    @Test
    void getUserProfileByUsername_adminFound_returnsOptionalWithAdmin() {
        when(adminRepository.findByUsername("adminuser")).thenReturn(testAdmin);

        Optional<AbstractUser> result = profileService.getUserProfileByUsername("adminuser");

        assertTrue(result.isPresent());
        assertEquals(testAdmin, result.get());
        verify(adminRepository, times(1)).findByUsername("adminuser");
        verify(customerRepository, never()).findByUsername(anyString());
        verify(driverRepository, never()).findByUsername(anyString());
    }

    @Test
    void getUserProfileByUsername_customerFound_returnsOptionalWithCustomer() {
        when(adminRepository.findByUsername("customeruser")).thenReturn(null);
        when(customerRepository.findByUsername("customeruser")).thenReturn(testCustomer);

        Optional<AbstractUser> result = profileService.getUserProfileByUsername("customeruser");

        assertTrue(result.isPresent());
        assertEquals(testCustomer, result.get());
        verify(adminRepository, times(1)).findByUsername("customeruser");
        verify(customerRepository, times(1)).findByUsername("customeruser");
        verify(driverRepository, never()).findByUsername(anyString());
    }

    @Test
    void getUserProfileByUsername_driverFound_returnsOptionalWithDriver() {
        when(adminRepository.findByUsername("driveruser")).thenReturn(null);
        when(customerRepository.findByUsername("driveruser")).thenReturn(null);
        when(driverRepository.findByUsername("driveruser")).thenReturn(testDriver);

        Optional<AbstractUser> result = profileService.getUserProfileByUsername("driveruser");

        assertTrue(result.isPresent());
        assertEquals(testDriver, result.get());
        verify(adminRepository, times(1)).findByUsername("driveruser");
        verify(customerRepository, times(1)).findByUsername("driveruser");
        verify(driverRepository, times(1)).findByUsername("driveruser");
    }

    @Test
    void getUserProfileByUsername_userNotFound_returnsEmptyOptional() {
        when(adminRepository.findByUsername("nonexistent")).thenReturn(null);
        when(customerRepository.findByUsername("nonexistent")).thenReturn(null);
        when(driverRepository.findByUsername("nonexistent")).thenReturn(null);

        Optional<AbstractUser> result = profileService.getUserProfileByUsername("nonexistent");

        assertFalse(result.isPresent());
    }

    @Test
    void updateUserProfile_customerProfile_updatesAndReturnsCustomer() {
        when(adminRepository.findByUsername("customeruser")).thenReturn(null);
        when(customerRepository.findByUsername("customeruser")).thenReturn(testCustomer);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);
    
        AbstractUser updatedUser = profileService.updateUserProfile("customeruser", updateRequest);
    
        assertEquals("98765", updatedUser.getMobileNumber());
        assertEquals("New Address", updatedUser.getAddress());
        verify(customerRepository, times(1)).save(testCustomer);
    }
    
    @Test
    void updateUserProfile_driverProfile_updatesAndReturnsDriver() {
        when(adminRepository.findByUsername("driveruser")).thenReturn(null);
        when(customerRepository.findByUsername("driveruser")).thenReturn(null);
        when(driverRepository.findByUsername("driveruser")).thenReturn(testDriver);
        when(driverRepository.save(any(Driver.class))).thenReturn(testDriver);
    
        AbstractUser updatedUser = profileService.updateUserProfile("driveruser", updateRequest);
    
        assertEquals("NEW_LIC", ((Driver) updatedUser).getLicenceNo());
        verify(driverRepository, times(1)).save(testDriver);
    }

    @Test
    void updateUserProfile_userNotFound_throwsException() {
        when(adminRepository.findByUsername("nonexistent")).thenReturn(null);
        when(customerRepository.findByUsername("nonexistent")).thenReturn(null);
        when(driverRepository.findByUsername("nonexistent")).thenReturn(null);
        
        assertThrows(IllegalArgumentException.class, () -> profileService.updateUserProfile("nonexistent", updateRequest));
    }
    
    @Test
    void isUsernameTaken_usernameExistsInAdminRepo_returnsTrue() {
        when(adminRepository.existsByUsername("adminuser")).thenReturn(true);
        boolean exists = profileService.isUsernameTaken("adminuser");
        assertTrue(exists);
        verify(adminRepository, times(1)).existsByUsername("adminuser");
    }

    @Test
    void isUsernameTaken_usernameExistsInCustomerRepo_returnsTrue() {
        when(adminRepository.existsByUsername("customeruser")).thenReturn(false);
        when(customerRepository.existsByUsername("customeruser")).thenReturn(true);
        boolean exists = profileService.isUsernameTaken("customeruser");
        assertTrue(exists);
        verify(adminRepository, times(1)).existsByUsername("customeruser");
        verify(customerRepository, times(1)).existsByUsername("customeruser");
    }

    @Test
    void isUsernameTaken_usernameExistsInDriverRepo_returnsTrue() {
        when(adminRepository.existsByUsername("driveruser")).thenReturn(false);
        when(customerRepository.existsByUsername("driveruser")).thenReturn(false);
        when(driverRepository.existsByUsername("driveruser")).thenReturn(true);
        boolean exists = profileService.isUsernameTaken("driveruser");
        assertTrue(exists);
        verify(adminRepository, times(1)).existsByUsername("driveruser");
        verify(customerRepository, times(1)).existsByUsername("driveruser");
        verify(driverRepository, times(1)).existsByUsername("driveruser");
    }

    @Test
    void isUsernameTaken_usernameDoesNotExist_returnsFalse() {
        when(adminRepository.existsByUsername("newuser")).thenReturn(false);
        when(customerRepository.existsByUsername("newuser")).thenReturn(false);
        when(driverRepository.existsByUsername("newuser")).thenReturn(false);
        boolean exists = profileService.isUsernameTaken("newuser");
        assertFalse(exists);
    }
}