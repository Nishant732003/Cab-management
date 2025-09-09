package com.cabbooking.service;

import com.cabbooking.model.AbstractUser;
import com.cabbooking.model.Admin;
import com.cabbooking.model.Customer;
import com.cabbooking.model.Driver;
import com.cabbooking.model.VerificationToken;
import com.cabbooking.repository.AdminRepository;
import com.cabbooking.repository.CustomerRepository;
import com.cabbooking.repository.DriverRepository;
import com.cabbooking.repository.VerificationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VerificationServiceImplTest {

    @Mock
    private VerificationTokenRepository tokenRepository;

    @Mock
    private IEmailService emailService;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private VerificationServiceImpl verificationService;

    private Admin testAdmin;
    private Customer testCustomer;
    private Driver testDriver;
    private VerificationToken testToken;

    @BeforeEach
    void setUp() {
        testAdmin = new Admin();
        testAdmin.setEmail("admin@test.com");
        testAdmin.setEmailVerified(false);
        testAdmin.setUsername("adminuser");

        testCustomer = new Customer();
        testCustomer.setEmail("customer@test.com");
        testCustomer.setEmailVerified(false);
        testCustomer.setUsername("customeruser");

        testDriver = new Driver();
        testDriver.setEmail("driver@test.com");
        testDriver.setEmailVerified(false);
        testDriver.setUsername("driveruser");

        testToken = new VerificationToken();
        testToken.setToken("valid-token");
        testToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        testToken.setUserEmail("test@test.com");
    }

    @Test
    void sendVerificationLink_validEmailForCustomer_savesTokenAndSendsEmail() {
        when(adminRepository.findByEmail(anyString())).thenReturn(null);
        when(customerRepository.findByEmail("customer@test.com")).thenReturn(testCustomer);
        doNothing().when(emailService).sendSimpleEmail(anyString(), anyString(), anyString());

        try (MockedStatic<UUID> mockedUuid = mockStatic(UUID.class)) {
            mockedUuid.when(UUID::randomUUID).thenReturn(UUID.fromString("12345678-1234-1234-1234-1234567890ab"));
            
            verificationService.sendVerificationLink("customer@test.com");
            
            verify(tokenRepository, times(1)).save(any(VerificationToken.class));
            verify(emailService, times(1)).sendSimpleEmail(eq("customer@test.com"), anyString(), anyString());
        }
    }
    
    @Test
    void sendVerificationLink_validEmailForAdmin_savesTokenAndSendsEmail() {
        when(adminRepository.findByEmail("admin@test.com")).thenReturn(testAdmin);
        doNothing().when(emailService).sendSimpleEmail(anyString(), anyString(), anyString());
        
        try (MockedStatic<UUID> mockedUuid = mockStatic(UUID.class)) {
            mockedUuid.when(UUID::randomUUID).thenReturn(UUID.fromString("12345678-1234-1234-1234-1234567890ab"));
            
            verificationService.sendVerificationLink("admin@test.com");
            
            verify(tokenRepository, times(1)).save(any(VerificationToken.class));
            verify(emailService, times(1)).sendSimpleEmail(eq("admin@test.com"), anyString(), anyString());
        }
    }
    
    @Test
    void sendVerificationLink_validEmailForDriver_savesTokenAndSendsEmail() {
        when(adminRepository.findByEmail("driver@test.com")).thenReturn(null);
        when(customerRepository.findByEmail("driver@test.com")).thenReturn(null);
        when(driverRepository.findByEmail("driver@test.com")).thenReturn(testDriver);
        doNothing().when(emailService).sendSimpleEmail(anyString(), anyString(), anyString());
        
        try (MockedStatic<UUID> mockedUuid = mockStatic(UUID.class)) {
            mockedUuid.when(UUID::randomUUID).thenReturn(UUID.fromString("12345678-1234-1234-1234-1234567890ab"));
            
            verificationService.sendVerificationLink("driver@test.com");
            
            verify(tokenRepository, times(1)).save(any(VerificationToken.class));
            verify(emailService, times(1)).sendSimpleEmail(eq("driver@test.com"), anyString(), anyString());
        }
    }

    @Test
    void sendVerificationLink_userNotFound_throwsIllegalArgumentException() {
        when(adminRepository.findByEmail(anyString())).thenReturn(null);
        when(customerRepository.findByEmail(anyString())).thenReturn(null);
        when(driverRepository.findByEmail(anyString())).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> verificationService.sendVerificationLink("nonexistent@test.com"));
        verify(tokenRepository, never()).save(any(VerificationToken.class));
        verify(emailService, never()).sendSimpleEmail(anyString(), anyString(), anyString());
    }
    
    @Test
    void sendVerificationLink_userAlreadyVerified_throwsIllegalStateException() {
        testCustomer.setEmailVerified(true);
        when(customerRepository.findByEmail("customer@test.com")).thenReturn(testCustomer);

        assertThrows(IllegalStateException.class, () -> verificationService.sendVerificationLink("customer@test.com"));
        verify(tokenRepository, never()).save(any(VerificationToken.class));
        verify(emailService, never()).sendSimpleEmail(anyString(), anyString(), anyString());
    }

    @Test
    void verifyToken_validToken_verifiesUserAndReturnsTrue() {
        VerificationToken validToken = new VerificationToken("valid-token", LocalDateTime.now().plusHours(1), "customer@test.com");
        when(tokenRepository.findByToken("valid-token")).thenReturn(validToken);
        when(customerRepository.findByEmail("customer@test.com")).thenReturn(testCustomer);

        boolean isVerified = verificationService.verifyToken("valid-token");

        assertTrue(isVerified);
        assertTrue(testCustomer.getEmailVerified());
        verify(customerRepository, times(1)).save(testCustomer);
        verify(tokenRepository, times(1)).delete(validToken);
    }
    
    @Test
    void verifyToken_invalidToken_returnsFalse() {
        when(tokenRepository.findByToken("invalid-token")).thenReturn(null);

        boolean isVerified = verificationService.verifyToken("invalid-token");

        assertFalse(isVerified);
        verify(customerRepository, never()).save(any(Customer.class));
        verify(tokenRepository, never()).delete(any(VerificationToken.class));
    }
    
    @Test
    void verifyToken_expiredToken_returnsFalse() {
        VerificationToken expiredToken = new VerificationToken("expired-token", LocalDateTime.now().minusHours(1), "customer@test.com");
        when(tokenRepository.findByToken("expired-token")).thenReturn(expiredToken);

        boolean isVerified = verificationService.verifyToken("expired-token");

        assertFalse(isVerified);
        verify(customerRepository, never()).save(any(Customer.class));
        verify(tokenRepository, never()).delete(any(VerificationToken.class));
    }
    
    @Test
    void verifyToken_userNotFoundForTokenEmail_throwsIllegalArgumentException() {
        VerificationToken validToken = new VerificationToken("valid-token", LocalDateTime.now().plusHours(1), "nonexistent@test.com");
        when(tokenRepository.findByToken("valid-token")).thenReturn(validToken);
        when(adminRepository.findByEmail("nonexistent@test.com")).thenReturn(null);
        when(customerRepository.findByEmail("nonexistent@test.com")).thenReturn(null);
        when(driverRepository.findByEmail("nonexistent@test.com")).thenReturn(null);
        
        assertThrows(IllegalArgumentException.class, () -> verificationService.verifyToken("valid-token"));
        verify(customerRepository, never()).save(any(Customer.class));
        verify(tokenRepository, never()).delete(any(VerificationToken.class));
    }
}