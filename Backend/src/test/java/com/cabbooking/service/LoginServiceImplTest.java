package com.cabbooking.service;

import com.cabbooking.dto.LoginRequest;
import com.cabbooking.dto.LoginResponse;
import com.cabbooking.model.Admin;
import com.cabbooking.model.Customer;
import com.cabbooking.model.Driver;
import com.cabbooking.repository.AdminRepository;
import com.cabbooking.repository.CustomerRepository;
import com.cabbooking.repository.DriverRepository;
import com.cabbooking.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for LoginServiceImpl.
 *
 * Tests cover authentication and user retrieval logic for Admin, Driver, and Customer entities:
 * - login() by username and email
 * - Handling invalid credentials
 * - Handling users not found after authentication
 * - loadUserByUsername() for different user types
 *
 * Dependencies:
 * - AdminRepository, DriverRepository, CustomerRepository: Mocked to simulate database operations
 * - AuthenticationManager: Mocked to simulate authentication
 * - JwtUtil: Mocked to simulate JWT token generation
 */
@ExtendWith(MockitoExtension.class)
public class LoginServiceImplTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private LoginServiceImpl loginService;

    private LoginRequest validLoginRequest;
    private Admin testAdmin;
    private Driver testDriver;
    private Customer testCustomer;

    /**
     * Sets up common test data before each test.
     * Initializes sample Admin, Driver, and Customer objects along with a valid login request.
     */
    @BeforeEach
    void setUp() {
        validLoginRequest = new LoginRequest();
        validLoginRequest.setUsername("testuser");
        validLoginRequest.setPassword("password123");

        testAdmin = new Admin();
        testAdmin.setId(1);
        testAdmin.setUsername("testadmin");
        testAdmin.setPassword("hashedPassword");

        testDriver = new Driver();
        testDriver.setId(2);
        testDriver.setUsername("testdriver");
        testDriver.setPassword("hashedPassword");

        testCustomer = new Customer();
        testCustomer.setId(3);
        testCustomer.setUsername("testcustomer");
        testCustomer.setPassword("hashedPassword");
    }

    /**
     * Tests successful admin login by username.
     *
     * Workflow:
     * - Mocks authentication manager to pass authentication
     * - Mocks admin repository to find the admin by username
     * - Mocks JwtUtil to generate a token
     * - Asserts LoginResponse fields
     * - Verifies repository call
     */
    @Test
    void login_adminLoginByUsername_returnsSuccessResponse() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(adminRepository.findByUsername("testuser")).thenReturn(testAdmin);
        when(jwtUtil.generateToken(testAdmin.getUsername(), "Admin")).thenReturn("admin-token");

        LoginResponse response = loginService.login(validLoginRequest);

        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertEquals("Admin", response.getUserType());
        assertEquals("admin-token", response.getToken());
        verify(adminRepository, times(1)).findByUsername("testuser");
    }

    /**
     * Tests successful driver login by username.
     *
     * Workflow:
     * - Mocks authentication to pass
     * - Mocks admin repository to return null
     * - Mocks driver repository to return the driver
     * - Mocks JwtUtil to generate token
     * - Asserts LoginResponse fields and verifies repository calls
     */
    @Test
    void login_driverLoginByUsername_returnsSuccessResponse() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(adminRepository.findByUsername("testuser")).thenReturn(null);
        when(driverRepository.findByUsername("testuser")).thenReturn(testDriver);
        when(jwtUtil.generateToken(testDriver.getUsername(), "Driver")).thenReturn("driver-token");

        LoginResponse response = loginService.login(validLoginRequest);

        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertEquals("Driver", response.getUserType());
        assertEquals("driver-token", response.getToken());
        verify(adminRepository, times(1)).findByUsername("testuser");
        verify(driverRepository, times(1)).findByUsername("testuser");
    }

    /**
     * Tests successful customer login by username.
     *
     * Workflow:
     * - Mocks authentication to pass
     * - Mocks admin and driver repositories to return null
     * - Mocks customer repository to return the customer
     * - Mocks JwtUtil to generate token
     * - Asserts LoginResponse fields and verifies repository calls
     */
    @Test
    void login_customerLoginByUsername_returnsSuccessResponse() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(adminRepository.findByUsername("testuser")).thenReturn(null);
        when(driverRepository.findByUsername("testuser")).thenReturn(null);
        when(customerRepository.findByUsername("testuser")).thenReturn(testCustomer);
        when(jwtUtil.generateToken(testCustomer.getUsername(), "Customer")).thenReturn("customer-token");

        LoginResponse response = loginService.login(validLoginRequest);

        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertEquals("Customer", response.getUserType());
        assertEquals("customer-token", response.getToken());
        verify(adminRepository, times(1)).findByUsername("testuser");
        verify(driverRepository, times(1)).findByUsername("testuser");
        verify(customerRepository, times(1)).findByUsername("testuser");
    }

    /**
     * Tests successful login by email instead of username.
     *
     * Workflow:
     * - Sets email in login request
     * - Mocks authentication to pass
     * - Mocks admin repository to find admin by email
     * - Mocks JwtUtil to generate token
     * - Asserts LoginResponse fields
     */
    @Test
    void login_byEmail_returnsSuccessResponse() {
        validLoginRequest.setUsername(null);
        validLoginRequest.setEmail("testadmin@test.com");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(adminRepository.findByEmail("testadmin@test.com")).thenReturn(testAdmin);
        when(jwtUtil.generateToken(testAdmin.getUsername(), "Admin")).thenReturn("admin-token");

        LoginResponse response = loginService.login(validLoginRequest);

        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertEquals("Admin", response.getUserType());
    }

    /**
     * Tests that login throws exception for invalid credentials.
     *
     * Workflow:
     * - Mocks authentication manager to throw BadCredentialsException
     * - Asserts that UsernameNotFoundException is thrown by login
     */
    @Test
    void login_invalidCredentials_throwsException() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(UsernameNotFoundException.class, () -> loginService.login(validLoginRequest));
    }

    /**
     * Tests that login throws exception when no user is found after successful authentication.
     *
     * Workflow:
     * - Mocks authentication to pass
     * - Mocks admin, driver, and customer repositories to return null
     * - Asserts that UsernameNotFoundException is thrown
     */
    @Test
    void login_userNotFoundAfterAuthentication_throwsException() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(adminRepository.findByUsername("testuser")).thenReturn(null);
        when(driverRepository.findByUsername("testuser")).thenReturn(null);
        when(customerRepository.findByUsername("testuser")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> loginService.login(validLoginRequest));
    }

    /**
     * Tests loadUserByUsername() for Admin.
     *
     * Workflow:
     * - Mocks admin repository to find admin
     * - Asserts returned UserDetails username
     */
    @Test
    void loadUserByUsername_adminFound_returnsUserDetails() {
        when(adminRepository.findByUsername("testadmin")).thenReturn(testAdmin);
        UserDetails userDetails = loginService.loadUserByUsername("testadmin");

        assertNotNull(userDetails);
        assertEquals("testadmin", userDetails.getUsername());
    }

    /**
     * Tests loadUserByUsername() for Driver.
     *
     * Workflow:
     * - Mocks admin repository to return null
     * - Mocks driver repository to return driver
     * - Asserts returned UserDetails username
     */
    @Test
    void loadUserByUsername_driverFound_returnsUserDetails() {
        when(adminRepository.findByUsername("testdriver")).thenReturn(null);
        when(driverRepository.findByUsername("testdriver")).thenReturn(testDriver);
        UserDetails userDetails = loginService.loadUserByUsername("testdriver");

        assertNotNull(userDetails);
        assertEquals("testdriver", userDetails.getUsername());
    }

    /**
     * Tests loadUserByUsername() for Customer.
     *
     * Workflow:
     * - Mocks admin and driver repositories to return null
     * - Mocks customer repository to return customer
     * - Asserts returned UserDetails username
     */
    @Test
    void loadUserByUsername_customerFound_returnsUserDetails() {
        when(adminRepository.findByUsername("testcustomer")).thenReturn(null);
        when(driverRepository.findByUsername("testcustomer")).thenReturn(null);
        when(customerRepository.findByUsername("testcustomer")).thenReturn(testCustomer);
        UserDetails userDetails = loginService.loadUserByUsername("testcustomer");

        assertNotNull(userDetails);
        assertEquals("testcustomer", userDetails.getUsername());
    }

    /**
     * Tests loadUserByUsername() throws exception when user is not found.
     *
     * Workflow:
     * - Mocks all repositories to return null
     * - Asserts UsernameNotFoundException is thrown
     */
    @Test
    void loadUserByUsername_userNotFound_throwsException() {
        when(adminRepository.findByUsername("nonexistent")).thenReturn(null);
        when(driverRepository.findByUsername("nonexistent")).thenReturn(null);
        when(customerRepository.findByUsername("nonexistent")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> loginService.loadUserByUsername("nonexistent"));
    }
}
