package com.cabbooking.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cabbooking.model.AbstractUser;
import com.cabbooking.model.Admin;
import com.cabbooking.model.Customer;
import com.cabbooking.model.Driver;
import com.cabbooking.model.VerificationToken;
import com.cabbooking.repository.AdminRepository;
import com.cabbooking.repository.CustomerRepository;
import com.cabbooking.repository.DriverRepository;
import com.cabbooking.repository.VerificationTokenRepository;

/*
 * Service for handling email verification operations.
 * 
 * Main Responsibilities:
 * - Sending verification links to users via email.
 * - Verifying users based on tokens received from email links.
 * 
 * Security:
 * - VerificationTokenRepository: Manages verification tokens in the database.
 * - IEmailService: Handles sending emails to users.
 * - AdminRepository, CustomerRepository, DriverRepository: Access user data for verification purposes.
 * - CustomerRepository: Access customer data for verification purposes.
 * - DriverRepository: Access driver data for verification purposes.
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
     * Sends a verification link to the specified email address.
     * 
     * Workflow:
     * - Checks if the user with the given email exists and is not already verified.
     * - Generates a unique verification token and saves it in the database with an expiry time.
     * - Sends an email to the user with a link to verify their email address.
     * - Throws IllegalArgumentException if the user is not found.
     * - Throws IllegalStateException if the email is already verified.
     * 
     * @param email The email address to send the verification link to.
     * @throws IllegalArgumentException if the user with the given email is not found.
     * @throws IllegalStateException if the email is already verified.
     */
    @Override
    @Transactional
    public void sendVerificationLink(String email) {
        // Find the user by email
        AbstractUser user = findUserByEmail(email).orElseThrow(() -> new IllegalArgumentException("User with email " + email + " not found."));
        if (Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new IllegalStateException("Email is already verified.");
        }

        // Create a token
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, LocalDateTime.now().plusHours(24), user.getEmail());
        tokenRepository.save(verificationToken);

        // Craft the email and send it
        String subject = "Verify Your Email for Cab Booking";
        String verificationUrl = "http://localhost:8080/verify-email?token=" + token;
        String message = "Please click the following link to verify your email address:\n" + verificationUrl;

        emailService.sendSimpleEmail(user.getEmail(), subject, message);
    }

    /**
     * Verifies a user's email using the provided token.
     * 
     * Workflow:
     * - Looks up the token in the database.
     * - Checks if the token exists and is not expired.
     * - If valid, retrieves the associated user and marks their email as verified.
     * - Deletes the used token from the database.
     * - Returns true if verification is successful, false otherwise.
     * - Throws IllegalArgumentException if the user associated with the token is not found.
     * 
     * @param token The verification token to validate.
     * @return true if the token is valid and the email is verified, false otherwise.
     * @throws IllegalArgumentException if the user associated with the token is not found.
     */
    @Override
    @Transactional
    public boolean verifyToken(String token) {
        // Find the token in the database
        VerificationToken verificationToken = tokenRepository.findByToken(token);
        if (verificationToken == null || verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return false; // Token is invalid or expired
        }

        // Get the user and update their status
        String userEmail = verificationToken.getUserEmail();
        AbstractUser user = findUserByEmail(userEmail).orElseThrow(() -> new IllegalArgumentException("User associated with token not found."));
        user.setEmailVerified(true);
        saveUser(user); // Save the updated user status

        // Delete the used token
        tokenRepository.delete(verificationToken);
        return true;
    }

    /* ==============
     * HELPER METHODS
     * ==============
     */

    /*
     * Helper method to find a user by email across all user types.
     */
    private Optional<AbstractUser> findUserByEmail(String email) {

        Optional<Admin> admin = Optional.ofNullable(adminRepository.findByEmail(email));
        if (admin.isPresent()) {
            return Optional.of(admin.get());
        }
        
        Optional<Customer> customer = Optional.ofNullable(customerRepository.findByEmail(email));
        if (customer.isPresent()) {
            return Optional.of(customer.get());
        }

        Optional<Driver> driver = Optional.ofNullable(driverRepository.findByEmail(email));
        if (driver.isPresent()) {
            return Optional.of(driver.get());
        }

        return Optional.empty();
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