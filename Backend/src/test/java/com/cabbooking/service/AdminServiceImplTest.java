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

@ExtendWith(MockitoExtension.class)
public class AdminServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private DriverRepository driverRepository;
    
    @InjectMocks
    private AdminServiceImpl adminService;

    private Customer testCustomer;
    private Driver testDriver;

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