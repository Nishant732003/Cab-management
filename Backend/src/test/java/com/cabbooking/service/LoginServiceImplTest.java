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

    @Test
    void login_invalidCredentials_throwsException() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(UsernameNotFoundException.class, () -> loginService.login(validLoginRequest));
    }

    @Test
    void login_userNotFoundAfterAuthentication_throwsException() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(adminRepository.findByUsername("testuser")).thenReturn(null);
        when(driverRepository.findByUsername("testuser")).thenReturn(null);
        when(customerRepository.findByUsername("testuser")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> loginService.login(validLoginRequest));
    }

    @Test
    void loadUserByUsername_adminFound_returnsUserDetails() {
        when(adminRepository.findByUsername("testadmin")).thenReturn(testAdmin);
        UserDetails userDetails = loginService.loadUserByUsername("testadmin");

        assertNotNull(userDetails);
        assertEquals("testadmin", userDetails.getUsername());
    }

    @Test
    void loadUserByUsername_driverFound_returnsUserDetails() {
        when(adminRepository.findByUsername("testdriver")).thenReturn(null);
        when(driverRepository.findByUsername("testdriver")).thenReturn(testDriver);
        UserDetails userDetails = loginService.loadUserByUsername("testdriver");

        assertNotNull(userDetails);
        assertEquals("testdriver", userDetails.getUsername());
    }

    @Test
    void loadUserByUsername_customerFound_returnsUserDetails() {
        when(adminRepository.findByUsername("testcustomer")).thenReturn(null);
        when(driverRepository.findByUsername("testcustomer")).thenReturn(null);
        when(customerRepository.findByUsername("testcustomer")).thenReturn(testCustomer);
        UserDetails userDetails = loginService.loadUserByUsername("testcustomer");

        assertNotNull(userDetails);
        assertEquals("testcustomer", userDetails.getUsername());
    }
    
    @Test
    void loadUserByUsername_userNotFound_throwsException() {
        when(adminRepository.findByUsername("nonexistent")).thenReturn(null);
        when(driverRepository.findByUsername("nonexistent")).thenReturn(null);
        when(customerRepository.findByUsername("nonexistent")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> loginService.loadUserByUsername("nonexistent"));
    }
}