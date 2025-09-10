package com.cabbooking.service;

import com.cabbooking.model.BlacklistedToken;
import com.cabbooking.repository.BlacklistedTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for LogoutServiceImpl.
 *
 * Tests cover token blacklisting functionality:
 * - blacklistToken(): Ensures a JWT token is saved to the BlacklistedToken repository
 *
 * Dependencies:
 * - BlacklistedTokenRepository: Mocked to simulate database save operation
 */
@ExtendWith(MockitoExtension.class)
public class LogoutServiceImplTest {

    // Mocked repository to simulate saving blacklisted tokens
    @Mock
    private BlacklistedTokenRepository blacklistedTokenRepository;

    // Service under test with mocked dependency injected
    @InjectMocks
    private LogoutServiceImpl logoutService;

    /**
     * Sets up common test environment.
     * Currently, no pre-setup is required.
     */
    @BeforeEach
    void setUp() {
        // No specific setup needed for these tests
    }

    /**
     * Tests that blacklistToken() successfully saves a token to the repository.
     *
     * Workflow:
     * - Calls blacklistToken() with a sample JWT string
     * - Verifies that BlacklistedTokenRepository.save() is called exactly once with any BlacklistedToken object
     */
    @Test
    void blacklistToken_savesTokenToRepository() {
        String token = "test-jwt-token";

        // Call method under test
        logoutService.blacklistToken(token);

        // Verify that repository save() was invoked once
        verify(blacklistedTokenRepository, times(1)).save(any(BlacklistedToken.class));
    }
}
