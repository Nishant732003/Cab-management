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

@ExtendWith(MockitoExtension.class)
public class AdminVerificationServiceImplTest {

    @Mock
    private AdminRepository adminRepository;

    @InjectMocks
    private AdminVerificationServiceImpl adminVerificationService;

    private Admin unverifiedAdmin;
    private Admin verifiedAdmin;

    @BeforeEach
    void setUp() {
        unverifiedAdmin = new Admin();
        unverifiedAdmin.setId(1);
        unverifiedAdmin.setVerified(false);

        verifiedAdmin = new Admin();
        verifiedAdmin.setId(2);
        verifiedAdmin.setVerified(true);
    }

    @Test
    void getUnverifiedAdmins_returnsListOfUnverifiedAdmins() {
        when(adminRepository.findByVerifiedFalse()).thenReturn(Collections.singletonList(unverifiedAdmin));

        List<Admin> admins = adminVerificationService.getUnverifiedAdmins();

        assertNotNull(admins);
        assertEquals(1, admins.size());
        assertFalse(admins.get(0).getVerified());
        verify(adminRepository, times(1)).findByVerifiedFalse();
    }

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

    @Test
    void verifyAdmin_invalidId_throwsException() {
        when(adminRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> adminVerificationService.verifyAdmin(99));
        verify(adminRepository, times(1)).findById(99);
        verify(adminRepository, never()).save(any(Admin.class));
    }
}