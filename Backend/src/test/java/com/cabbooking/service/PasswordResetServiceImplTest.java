package com.cabbooking.service;

import com.cabbooking.model.Customer;
import com.cabbooking.model.PasswordResetToken;
import com.cabbooking.repository.AdminRepository;
import com.cabbooking.repository.CustomerRepository;
import com.cabbooking.repository.DriverRepository;
import com.cabbooking.repository.PasswordResetTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PasswordResetServiceImplTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private IEmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordResetServiceImpl passwordResetService;

    private Customer testCustomer;
    private PasswordResetToken testToken;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setEmail("test@test.com");
        testCustomer.setPassword("oldHashedPassword");
    }
    
    @Test
    void createAndSendPasswordResetToken_validEmail_sendsEmailAndSavesToken() {
        when(customerRepository.findByEmail(anyString())).thenReturn(testCustomer);
        doNothing().when(emailService).sendSimpleEmail(anyString(), anyString(), anyString());

        passwordResetService.createAndSendPasswordResetToken("test@test.com");
        
        verify(customerRepository, times(1)).findByEmail("test@test.com");
        verify(tokenRepository, times(1)).save(any(PasswordResetToken.class));
        verify(emailService, times(1)).sendSimpleEmail(anyString(), anyString(), anyString());
    }

    @Test
    void createAndSendPasswordResetToken_emailNotFound_throwsIllegalStateException() {
        when(customerRepository.findByEmail(anyString())).thenReturn(null);
        when(adminRepository.findByEmail(anyString())).thenReturn(null);
        when(driverRepository.findByEmail(anyString())).thenReturn(null);

        assertThrows(IllegalStateException.class, () -> passwordResetService.createAndSendPasswordResetToken("nonexistent@test.com"));
        verify(tokenRepository, never()).save(any(PasswordResetToken.class));
        verify(emailService, never()).sendSimpleEmail(anyString(), anyString(), anyString());
    }

    @Test
    void resetPassword_validToken_resetsPassword() {
        PasswordResetToken validToken = new PasswordResetToken("validToken", LocalDateTime.now().plusHours(1), "test@test.com");
        when(tokenRepository.findByToken("validToken")).thenReturn(validToken);
        when(customerRepository.findByEmail("test@test.com")).thenReturn(testCustomer);
        when(passwordEncoder.encode("newPassword")).thenReturn("newHashedPassword");

        boolean success = passwordResetService.resetPassword("validToken", "newPassword");

        assertTrue(success);
        assertEquals("newHashedPassword", testCustomer.getPassword());
        verify(customerRepository, times(1)).save(testCustomer);
        verify(tokenRepository, times(1)).delete(validToken);
    }

    @Test
    void resetPassword_invalidToken_returnsFalse() {
        when(tokenRepository.findByToken("invalidToken")).thenReturn(null);

        boolean success = passwordResetService.resetPassword("invalidToken", "newPassword");

        assertFalse(success);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void resetPassword_expiredToken_returnsFalse() {
        PasswordResetToken expiredToken = new PasswordResetToken("expiredToken", LocalDateTime.now().minusHours(1), "test@test.com");
        when(tokenRepository.findByToken("expiredToken")).thenReturn(expiredToken);

        boolean success = passwordResetService.resetPassword("expiredToken", "newPassword");

        assertFalse(success);
        verify(customerRepository, never()).save(any(Customer.class));
    }
}