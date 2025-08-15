package com.cabbooking.service;

import com.cabbooking.model.AbstractUser;
import com.cabbooking.model.VerificationToken;
import com.cabbooking.repository.AdminRepository;
import com.cabbooking.repository.CustomerRepository;
import com.cabbooking.repository.DriverRepository;
import com.cabbooking.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cabbooking.model.Admin;
import com.cabbooking.model.Customer;
import com.cabbooking.model.Driver;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Optional;

/**
 * Service to manage the email verification process.
 */
@Service
public class VerificationServiceImpl implements IVerificationService {

    @Autowired
    private VerificationTokenRepository tokenRepository;
    @Autowired
    private IEmailService emailService;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private DriverRepository driverRepository;

    /**
     * Creates a verification token, saves it, and sends it to the user's email.
     *
     * @param email The email of the user to send the verification link to.
     */
    @Override
    @Transactional
    public void sendVerificationLink(String email) {
        // 1. Find the user by email
        AbstractUser user = findUserByEmail(email).orElseThrow(() -> new IllegalArgumentException("User with email " + email + " not found."));
        if (Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new IllegalStateException("Email is already verified.");
        }

        // 2. Create a token
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, LocalDateTime.now().plusHours(24), user.getEmail());
        tokenRepository.save(verificationToken);

        // 3. Craft the email and send it
        String subject = "Verify Your Email for Cab Booking";
        String verificationUrl = "http://localhost:8080/verify-email?token=" + token;
        String message = "Please click the following link to verify your email address:\n" + verificationUrl;

        emailService.sendSimpleEmail(user.getEmail(), subject, message);
    }

    /**
     * Verifies a user's email using the provided token.
     *
     * @param token The verification token from the email link.
     * @return True if verification is successful, false otherwise.
     */
    @Override
    @Transactional
    public boolean verifyToken(String token) {
        // 1. Find the token in the database
        VerificationToken verificationToken = tokenRepository.findByToken(token);
        if (verificationToken == null || verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return false; // Token is invalid or expired
        }

        // 2. Get the user and update their status
        String userEmail = verificationToken.getUserEmail();
        AbstractUser user = findUserByEmail(userEmail).orElseThrow(() -> new IllegalArgumentException("User associated with token not found."));
        user.setEmailVerified(true);
        saveUser(user); // Save the updated user status

        // 3. Delete the used token
        tokenRepository.delete(verificationToken);
        return true;
    }

    private Optional<AbstractUser> findUserByEmail(String email) {
        return adminRepository.findByEmail(email)
                .<AbstractUser>map(admin -> admin)
                .or(() -> customerRepository.findByEmail(email).map(customer -> customer))
                .or(() -> driverRepository.findByEmail(email).map(driver -> driver));
    }

    // Helper method to save a user regardless of their type
    private void saveUser(AbstractUser user) {
        if (user instanceof Admin) {
            adminRepository.save((Admin) user);
        } else if (user instanceof Customer) {
            customerRepository.save((Customer) user);
        } else if (user instanceof Driver) {
            driverRepository.save((Driver) user);
        }
    }
}
