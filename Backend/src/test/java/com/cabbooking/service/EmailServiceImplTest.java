package com.cabbooking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for EmailServiceImpl.
 *
 * Tests cover the functionality of sending simple emails.
 * Dependencies:
 * - JavaMailSender: Mocked to simulate email sending without actually sending emails.
 */
@ExtendWith(MockitoExtension.class)
public class EmailServiceImplTest {

    // Mocked JavaMailSender to simulate email sending
    @Mock
    private JavaMailSender mailSender;

    // Service under test with mocked dependency injected
    @InjectMocks
    private EmailServiceImpl emailService;

    /**
     * Sets up common pre-test configurations if needed.
     * Currently empty because no pre-setup is required.
     */
    @BeforeEach
    void setUp() {
        // No setup required for this test
    }

    /**
     * Tests that sendSimpleEmail() sends an email with the correct details.
     *
     * Workflow:
     * - Calls sendSimpleEmail() with recipient, subject, and body
     * - Verifies that the mailSender.send() method is called exactly once
     * - Optional: Can capture and assert the content of the sent email
     */
    @Test
    void sendSimpleEmail_sendsEmailWithCorrectDetails() {
        String to = "test@test.com";
        String subject = "Test Subject";
        String text = "Test Body";

        // Call the method under test
        emailService.sendSimpleEmail(to, subject, text);

        // Verify that mailSender.send() was called once with any SimpleMailMessage
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));

       
    }
}
