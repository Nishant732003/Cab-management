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

@ExtendWith(MockitoExtension.class)
public class LogoutServiceImplTest {

    @Mock
    private BlacklistedTokenRepository blacklistedTokenRepository;

    @InjectMocks
    private LogoutServiceImpl logoutService;

    @BeforeEach
    void setUp() {
        // No specific setup needed for these tests
    }

    @Test
    void blacklistToken_savesTokenToRepository() {
        String token = "test-jwt-token";
        
        logoutService.blacklistToken(token);
        
        // Verify that the save method was called exactly once with a BlacklistedToken object
        verify(blacklistedTokenRepository, times(1)).save(any(BlacklistedToken.class));
    }
}