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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PasswordResetServiceImpl.
 *
 * Tests cover functionality for password reset flows:
 * - Creating and sending password reset tokens
 * - Handling invalid or non-existent emails
 * - Resetting passwords with valid, invalid, and expired tokens
 *
 * Dependencies:
 * - AdminRepository, DriverRepository, CustomerRepository: Mocked to simulate database lookups
 * - PasswordResetTokenRepository: Mocked to simulate token creation, lookup, and deletion
 * - IEmailService: Mocked to simulate sending password reset emails
 * - PasswordEncoder: Mocked to simulate password hashing
 */
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

    /**
     * Sets up common test data before each test.
     * Initializes a Customer object with email and password.
     */
    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setEmail("test@test.com");
        testCustomer.setPassword("oldHashedPassword");
    }

    /**
     * Tests creating and sending a password reset token for a valid email.
     *
     * Workflow:
     * - Mocks customer repository to return a test customer
     * - Mocks email service to do nothing when sending email
     * - Calls createAndSendPasswordResetToken() with a valid email
     * - Verifies that customer lookup, token saving, and email sending are performed
     */
    @Test
    void createAndSendPasswordResetToken_validEmail_sendsEmailAndSavesToken() {
        when(customerRepository.findByEmail(anyString())).thenReturn(testCustomer);
        doNothing().when(emailService).sendSimpleEmail(anyString(), anyString(), anyString());

        passwordResetService.createAndSendPasswordResetToken("test@test.com");

        verify(customerRepository, times(1)).findByEmail("test@test.com");
        verify(tokenRepository, times(1)).save(any(PasswordResetToken.class));
        verify(emailService, times(1)).sendSimpleEmail(anyString(), anyString(), anyString());
    }

    /**
     * Tests that creating a password reset token for a non-existent email throws an exception.
     *
     * Workflow:
     * - Mocks all repositories to return null for the given email
     * - Asserts that createAndSendPasswordResetToken() throws IllegalStateException
     * - Verifies that token saving and email sending are never performed
     */
    @Test
    void createAndSendPasswordResetToken_emailNotFound_throwsIllegalStateException() {
        when(customerRepository.findByEmail(anyString())).thenReturn(null);
        when(adminRepository.findByEmail(anyString())).thenReturn(null);
        when(driverRepository.findByEmail(anyString())).thenReturn(null);

        assertThrows(IllegalStateException.class, () -> 
            passwordResetService.createAndSendPasswordResetToken("nonexistent@test.com")
        );

        verify(tokenRepository, never()).save(any(PasswordResetToken.class));
        verify(emailService, never()).sendSimpleEmail(anyString(), anyString(), anyString());
    }

    /**
     * Tests resetting password with a valid, non-expired token.
     *
     * Workflow:
     * - Mocks token repository to return a valid token
     * - Mocks customer repository to return the test customer
     * - Mocks password encoder to return hashed new password
     * - Calls resetPassword() with valid token and new password
     * - Asserts that password is updated, method returns true, and repositories are updated
     */
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

    /**
     * Tests resetting password with an invalid token.
     *
     * Workflow:
     * - Mocks token repository to return null for invalid token
     * - Calls resetPassword() and asserts it returns false
     * - Verifies that customer is not saved
     */
    @Test
    void resetPassword_invalidToken_returnsFalse() {
        when(tokenRepository.findByToken("invalidToken")).thenReturn(null);

        boolean success = passwordResetService.resetPassword("invalidToken", "newPassword");

        assertFalse(success);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    /**
     * Tests resetting password with an expired token.
     *
     * Workflow:
     * - Mocks token repository to return an expired token
     * - Calls resetPassword() and asserts it returns false
     * - Verifies that customer is not saved
     */
    @Test
    void resetPassword_expiredToken_returnsFalse() {
        PasswordResetToken expiredToken = new PasswordResetToken("expiredToken", LocalDateTime.now().minusHours(1), "test@test.com");
        when(tokenRepository.findByToken("expiredToken")).thenReturn(expiredToken);

        boolean success = passwordResetService.resetPassword("expiredToken", "newPassword");

        assertFalse(success);
        verify(customerRepository, never()).save(any(Customer.class));
    }
}
