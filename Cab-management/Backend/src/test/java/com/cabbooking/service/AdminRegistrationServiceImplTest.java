package com.cabbooking.service;

import com.cabbooking.dto.AdminRegistrationRequest;
import com.cabbooking.model.Admin;
import com.cabbooking.repository.AdminRepository;
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
 * Unit tests for AdminRegistrationServiceImpl.
 * 
 * Tests cover admin registration scenarios including:
 * - Successful registration with new username/email
 * - Registration with duplicate username
 * - Registration with duplicate email
 * 
 * Dependencies:
 * - AdminRepository: Mocked to simulate database operations
 * - PasswordEncoder: Mocked to simulate password encryption
 */
@ExtendWith(MockitoExtension.class)
public class AdminRegistrationServiceImplTest {

    // Mocked repository to simulate database interactions for Admin entities
    @Mock
    private AdminRepository adminRepository;

    // Mocked password encoder to simulate password hashing
    @Mock
    private PasswordEncoder passwordEncoder;

    // Service under test with mocked dependencies injected
    @InjectMocks
    private AdminRegistrationServiceImpl adminRegistrationService;

    // Test data objects for registration request and Admin entity
    private AdminRegistrationRequest testRequest;
    private Admin testAdmin;

    /**
     * Sets up test data before each test method runs.
     * 
     * Workflow:
     * - Creates a sample AdminRegistrationRequest
     * - Creates a sample Admin object
     */
    @BeforeEach
    void setUp() {
        testRequest = new AdminRegistrationRequest();
        testRequest.setUsername("testadmin");
        testRequest.setEmail("testadmin@test.com");
        testRequest.setPassword("password123");

        testAdmin = new Admin();
        testAdmin.setUsername("testadmin");
        testAdmin.setEmail("testadmin@test.com");
    }

    /**
     * Tests successful registration of a new admin.
     * 
     * Workflow:
     * - Mocks repository to indicate username/email are not taken
     * - Mocks password encoder to return encoded password
     * - Mocks repository save() to return the Admin object
     * - Asserts that the returned Admin is not null and has correct username
     * - Verifies that save() is called exactly once
     */
    @Test
    void registerAdmin_validRequest_returnsNewAdmin() {
        when(adminRepository.existsByUsername(anyString())).thenReturn(false);
        when(adminRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(adminRepository.save(any(Admin.class))).thenReturn(testAdmin);

        Admin newAdmin = adminRegistrationService.registerAdmin(testRequest);

        assertNotNull(newAdmin);
        assertEquals("testadmin", newAdmin.getUsername());

        verify(adminRepository, times(1)).save(any(Admin.class));
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
    void registerAdmin_duplicateUsername_throwsException() {
        when(adminRepository.existsByUsername("testadmin")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> adminRegistrationService.registerAdmin(testRequest));

        verify(adminRepository, never()).save(any(Admin.class));
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
    void registerAdmin_duplicateEmail_throwsException() {
        when(adminRepository.existsByUsername("testadmin")).thenReturn(false);
        when(adminRepository.existsByEmail("testadmin@test.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> adminRegistrationService.registerAdmin(testRequest));

        verify(adminRepository, never()).save(any(Admin.class));
    }
}
