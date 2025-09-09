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

@ExtendWith(MockitoExtension.class)
public class AdminRegistrationServiceImplTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminRegistrationServiceImpl adminRegistrationService;

    private AdminRegistrationRequest testRequest;
    private Admin testAdmin;

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

    @Test
    void registerAdmin_duplicateUsername_throwsException() {
        when(adminRepository.existsByUsername("testadmin")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> adminRegistrationService.registerAdmin(testRequest));
        verify(adminRepository, never()).save(any(Admin.class));
    }

    @Test
    void registerAdmin_duplicateEmail_throwsException() {
        when(adminRepository.existsByUsername("testadmin")).thenReturn(false);
        when(adminRepository.existsByEmail("testadmin@test.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> adminRegistrationService.registerAdmin(testRequest));
        verify(adminRepository, never()).save(any(Admin.class));
    }
}