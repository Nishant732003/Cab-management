package com.cabbooking.service;

import com.cabbooking.model.Admin;
import com.cabbooking.repository.AdminRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AdminVerificationServiceImpl.
 * 
 * Tests focus on verifying the behavior of admin verification operations, including:
 * - Fetching all unverified admins
 * - Verifying a specific admin by ID
 *
 * Dependencies:
 * - AdminRepository: Mocked to simulate database access for Admin entities
 *
 * Setup:
 * - Uses Mockito for dependency injection and behavior simulation
 * - Test data includes verified and unverified Admin objects
 */
@ExtendWith(MockitoExtension.class)
public class AdminVerificationServiceImplTest {

    // Mocked repository for admin database operations
    @Mock
    private AdminRepository adminRepository;

    // Service under test with mocked dependencies injected
    @InjectMocks
    private AdminVerificationServiceImpl adminVerificationService;

    // Test data objects
    private Admin unverifiedAdmin;
    private Admin verifiedAdmin;

    /**
     * Initializes test data before each test method runs.
     * 
     * Workflow:
     * - Creates a sample unverified admin
     * - Creates a sample verified admin
     * - Provides consistent test data for all test cases
     */
    @BeforeEach
    void setUp() {
        unverifiedAdmin = new Admin();
        unverifiedAdmin.setId(1);
        unverifiedAdmin.setVerified(false);

        verifiedAdmin = new Admin();
        verifiedAdmin.setId(2);
        verifiedAdmin.setVerified(true);
    }

    /**
     * Tests fetching all unverified admins.
     * 
     * Workflow:
     * - Mocks repository to return a list with a single unverified admin
     * - Calls getUnverifiedAdmins() and verifies that the returned list contains only unverified admins
     * - Ensures repository method is called exactly once
     */
    @Test
    void getUnverifiedAdmins_returnsListOfUnverifiedAdmins() {
        when(adminRepository.findByVerifiedFalse()).thenReturn(Collections.singletonList(unverifiedAdmin));

        List<Admin> admins = adminVerificationService.getUnverifiedAdmins();

        assertNotNull(admins);
        assertEquals(1, admins.size());
        assertFalse(admins.get(0).getVerified());

        verify(adminRepository, times(1)).findByVerifiedFalse();
    }

    /**
     * Tests verifying an admin with a valid ID.
     * 
     * Workflow:
     * - Mocks repository to return an unverified admin for the given ID
     * - Calls verifyAdmin() and checks that admin is marked as verified
     * - Verifies that the correct success message is returned
     * - Ensures findById and save repository methods are called exactly once
     */
    @Test
    void verifyAdmin_validId_verifiesAdminAndReturnsSuccessMessage() {
        when(adminRepository.findById(1)).thenReturn(Optional.of(unverifiedAdmin));
        when(adminRepository.save(any(Admin.class))).thenReturn(unverifiedAdmin);

        String message = adminVerificationService.verifyAdmin(1);

        assertEquals("Admin verified successfully", message);
        assertTrue(unverifiedAdmin.getVerified());

        verify(adminRepository, times(1)).findById(1);
        verify(adminRepository, times(1)).save(unverifiedAdmin);
    }

    /**
     * Tests verifying an admin with an invalid ID.
     * 
     * Workflow:
     * - Mocks repository to return empty Optional for the given ID
     * - Expects IllegalArgumentException when verifyAdmin() is called
     * - Ensures findById is called, but save is never called
     */
    @Test
    void verifyAdmin_invalidId_throwsException() {
        when(adminRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> adminVerificationService.verifyAdmin(99));

        verify(adminRepository, times(1)).findById(99);
        verify(adminRepository, never()).save(any(Admin.class));
    }
}
