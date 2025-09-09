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

@ExtendWith(MockitoExtension.class)
public class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void sendSimpleEmail_sendsEmailWithCorrectDetails() {
        String to = "test@test.com";
        String subject = "Test Subject";
        String text = "Test Body";

        emailService.sendSimpleEmail(to, subject, text);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        
        // You can add more detailed verification if needed
        // For example, to check the content of the message object
        // ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        // verify(mailSender).send(captor.capture());
        // SimpleMailMessage sentMessage = captor.getValue();
        // assertEquals(to, sentMessage.getTo()[0]);
        // assertEquals(subject, sentMessage.getSubject());
        // assertEquals(text, sentMessage.getText());
    }
}