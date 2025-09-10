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

/**
 * Unit tests for CustomerRegistrationServiceImpl.
 *
 * Tests cover customer registration scenarios including:
 * - Successful registration with new username/email
 * - Registration with duplicate username
 * - Registration with duplicate email
 *
 * Dependencies:
 * - CustomerRepository: Mocked to simulate database operations
 * - PasswordEncoder: Mocked to simulate password encryption
 */
@ExtendWith(MockitoExtension.class)
public class CustomerRegistrationServiceImplTest {

    // Mocked repository to simulate database interactions for Customer entities
    @Mock
    private CustomerRepository customerRepository;

    // Mocked password encoder to simulate password hashing
    @Mock
    private PasswordEncoder passwordEncoder;

    // Service under test with mocked dependencies injected
    @InjectMocks
    private CustomerRegistrationServiceImpl customerRegistrationService;

    // Test data objects for registration request and Customer entity
    private CustomerRegistrationRequest testRequest;
    private Customer testCustomer;

    /**
     * Sets up test data before each test method runs.
     *
     * Workflow:
     * - Creates a sample CustomerRegistrationRequest
     * - Creates a sample Customer object
     */
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

    /**
     * Tests successful registration of a new customer.
     *
     * Workflow:
     * - Mocks repository to indicate username/email are not taken
     * - Mocks password encoder to return encoded password
     * - Mocks repository save() to return the Customer object
     * - Asserts that the returned Customer is not null and has correct username
     * - Verifies that save() is called exactly once
     */
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

    /**
     * Tests registration failure due to duplicate username.
     *
     * Workflow:
     * - Mocks repository to indicate username already exists
     * - Asserts that an IllegalArgumentException is thrown
     * - Verifies that save() is never called
     */
    @Test
    void registerCustomer_duplicateUsername_throwsException() {
        when(customerRepository.existsByUsername("testcustomer")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> customerRegistrationService.registerCustomer(testRequest));
        verify(customerRepository, never()).save(any(Customer.class));
    }

    /**
     * Tests registration failure due to duplicate email.
     *
     * Workflow:
     * - Mocks repository to indicate email already exists while username is new
     * - Asserts that an IllegalArgumentException is thrown
     * - Verifies that save() is never called
     */
    @Test
    void registerCustomer_duplicateEmail_throwsException() {
        when(customerRepository.existsByUsername("testcustomer")).thenReturn(false);
        when(customerRepository.existsByEmail("customer@test.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> customerRegistrationService.registerCustomer(testRequest));
        verify(customerRepository, never()).save(any(Customer.class));
    }
}
