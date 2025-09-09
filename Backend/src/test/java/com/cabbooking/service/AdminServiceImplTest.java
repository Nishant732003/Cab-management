package com.cabbooking.service;

import com.cabbooking.dto.UserSummaryDTO;
import com.cabbooking.model.Customer;
import com.cabbooking.model.Driver;
import com.cabbooking.repository.CustomerRepository;
import com.cabbooking.repository.DriverRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private AdminServiceImpl adminService;

    private Customer customer;
    private Driver driver;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Sample customer
        customer = new Customer();
        customer.setId(1L);
        customer.setUsername("john123");
        customer.setEmail("john@example.com");
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setMobileNumber("9999999999");

        // Sample driver
        driver = new Driver();
        driver.setId(10L);
        driver.setUsername("driver01");
        driver.setFirstName("Mike");
        driver.setLastName("Smith");
        driver.setEmail("mike@example.com");
        driver.setMobileNumber("8888888888");
        driver.setLicenceNo("LIC12345");
        driver.setVerified(true);
        driver.setRating(4.5f);
    }

    // =============================
    // Tests for getAllCustomers()
    // =============================

    @Test
    void testGetAllCustomers_returnsCustomerList() {
        when(customerRepository.findAll()).thenReturn(Arrays.asList(customer));

        List<UserSummaryDTO> customers = adminService.getAllCustomers();

        assertNotNull(customers);
        assertEquals(1, customers.size());
        assertEquals("john123", customers.get(0).getUsername());
        assertEquals("9999999999", customers.get(0).getMobileNumber());

        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void testGetAllCustomers_returnsEmptyList() {
        when(customerRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserSummaryDTO> customers = adminService.getAllCustomers();

        assertNotNull(customers);
        assertTrue(customers.isEmpty());

        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void testGetAllCustomers_handlesRepositoryException() {
        when(customerRepository.findAll()).thenThrow(new RuntimeException("DB error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            adminService.getAllCustomers();
        });

        assertEquals("DB error", exception.getMessage());
        verify(customerRepository, times(1)).findAll();
    }

    // =============================
    // Tests for getAllDrivers()
    // =============================

    @Test
    void testGetAllDrivers_returnsDriverList() {
        when(driverRepository.findAll()).thenReturn(Arrays.asList(driver));

        List<UserSummaryDTO> drivers = adminService.getAllDrivers();

        assertNotNull(drivers);
        assertEquals(1, drivers.size());
        assertEquals("driver01", drivers.get(0).getUsername());
        assertEquals("LIC12345", drivers.get(0).getLicenceNo());
        assertEquals(4.5, drivers.get(0).getRating());

        verify(driverRepository, times(1)).findAll();
    }

    @Test
    void testGetAllDrivers_returnsEmptyList() {
        when(driverRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserSummaryDTO> drivers = adminService.getAllDrivers();

        assertNotNull(drivers);
        assertTrue(drivers.isEmpty());

        verify(driverRepository, times(1)).findAll();
    }

    @Test
    void testGetAllDrivers_handlesNullRatingGracefully() {
        driver.setRating(null);
        when(driverRepository.findAll()).thenReturn(Arrays.asList(driver));

        List<UserSummaryDTO> drivers = adminService.getAllDrivers();

        assertNotNull(drivers);
        assertEquals(1, drivers.size());
        assertEquals(0.0, drivers.get(0).getRating()); // fallback value

        verify(driverRepository, times(1)).findAll();
    }

    @Test
    void testGetAllDrivers_handlesRepositoryException() {
        when(driverRepository.findAll()).thenThrow(new RuntimeException("DB error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            adminService.getAllDrivers();
        });

        assertEquals("DB error", exception.getMessage());
        verify(driverRepository, times(1)).findAll();
    }
}
