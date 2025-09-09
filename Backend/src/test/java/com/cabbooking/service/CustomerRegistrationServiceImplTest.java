package com.cabbooking.service;

import com.cabbooking.dto.CustomerRegistrationRequest;
import com.cabbooking.model.Customer;
import com.cabbooking.repository.CustomerRepository;
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
public class CustomerRegistrationServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomerRegistrationServiceImpl customerRegistrationService;

    private CustomerRegistrationRequest testRequest;
    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testRequest = new CustomerRegistrationRequest();
        testRequest.setUsername("testcustomer");
        testRequest.setEmail("customer@test.com");
        testRequest.setPassword("password123");
        testRequest.setFirstName("Test");
        testRequest.setLastName("Customer");

        testCustomer = new Customer();
        testCustomer.setUsername("testcustomer");
        testCustomer.setEmail("customer@test.com");
    }

    @Test
    void registerCustomer_validRequest_returnsNewCustomer() {
        when(customerRepository.existsByUsername(anyString())).thenReturn(false);
        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        Customer newCustomer = customerRegistrationService.registerCustomer(testRequest);

        assertNotNull(newCustomer);
        assertEquals("testcustomer", newCustomer.getUsername());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void registerCustomer_duplicateUsername_throwsException() {
        when(customerRepository.existsByUsername("testcustomer")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> customerRegistrationService.registerCustomer(testRequest));
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void registerCustomer_duplicateEmail_throwsException() {
        when(customerRepository.existsByUsername("testcustomer")).thenReturn(false);
        when(customerRepository.existsByEmail("customer@test.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> customerRegistrationService.registerCustomer(testRequest));
        verify(customerRepository, never()).save(any(Customer.class));
    }
}