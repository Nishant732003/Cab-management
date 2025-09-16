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
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AdminServiceImpl.
 *
 * Tests focus on verifying the behavior of admin service methods that retrieve
 * summaries of customers and drivers.
 *
 * Dependencies:
 * - CustomerRepository: Mocked to simulate database access for Customer entities
 * - DriverRepository: Mocked to simulate database access for Driver entities
 *
 * Setup:
 * - Uses Mockito to inject mocked repositories into the service
 * - Test data includes sample Customer and Driver objects
 */
@ExtendWith(MockitoExtension.class)
public class AdminServiceImplTest {

    // Mocked repository for Customer database operations
    @Mock
    private CustomerRepository customerRepository;

    // Mocked repository for Driver database operations
    @Mock
    private DriverRepository driverRepository;

    // Service under test, with mocked repositories injected
    @InjectMocks
    private AdminServiceImpl adminService;

    // Test data objects for Customer and Driver
    private Customer testCustomer;
    private Driver testDriver;

    /**
     * Sets up test data before each test method runs.
     *
     * Workflow:
     * - Creates a sample Customer object
     * - Creates a sample Driver object
     * - Provides consistent test data for all test cases
     */
    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setId(1);
        testCustomer.setUsername("customeruser");
        testCustomer.setFirstName("John");
        testCustomer.setEmail("john@test.com");
        testCustomer.setMobileNumber("1234567890");

        testDriver = new Driver();
        testDriver.setId(2);
        testDriver.setUsername("driveruser");
        testDriver.setFirstName("Jane");
        testDriver.setEmail("jane@test.com");
        testDriver.setMobileNumber("0987654321");
        testDriver.setRating(4.8f);
        testDriver.setLicenceNo("LIC123");
        testDriver.setVerified(true);
    }

    /**
     * Tests that getAllCustomers() correctly retrieves all customers
     * and maps them to UserSummaryDTO objects.
     *
     * Workflow:
     * - Mocks customerRepository.findAll() to return a list with testCustomer
     * - Calls getAllCustomers() and verifies returned list
     * - Ensures username and first name are correctly mapped
     * - Verifies repository interaction
     */
    @Test
    void getAllCustomers_returnsListOfUserSummaryDTO() {
        when(customerRepository.findAll()).thenReturn(Arrays.asList(testCustomer));

        List<UserSummaryDTO> customerSummaries = adminService.getAllCustomers();

        assertNotNull(customerSummaries);
        assertEquals(1, customerSummaries.size());
        UserSummaryDTO summary = customerSummaries.get(0);
        assertEquals(testCustomer.getUsername(), summary.getUsername());
        assertEquals(testCustomer.getFirstName(), summary.getFirstName());

        verify(customerRepository, times(1)).findAll();
    }

    /**
     * Tests that getAllDrivers() correctly retrieves all drivers
     * and maps them to UserSummaryDTO objects.
     *
     * Workflow:
     * - Mocks driverRepository.findAll() to return a list with testDriver
     * - Calls getAllDrivers() and verifies returned list
     * - Ensures username and rating are correctly mapped
     * - Verifies repository interaction
     */
    @Test
    void getAllDrivers_returnsListOfUserSummaryDTO() {
        when(driverRepository.findAll()).thenReturn(Arrays.asList(testDriver));

        List<UserSummaryDTO> driverSummaries = adminService.getAllDrivers();

        assertNotNull(driverSummaries);
        assertEquals(1, driverSummaries.size());
        UserSummaryDTO summary = driverSummaries.get(0);
        assertEquals(testDriver.getUsername(), summary.getUsername());
        assertEquals(testDriver.getRating().doubleValue(), summary.getRating());

        verify(driverRepository, times(1)).findAll();
    }
}
