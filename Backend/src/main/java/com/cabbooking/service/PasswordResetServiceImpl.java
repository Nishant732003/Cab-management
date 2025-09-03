package com.cabbooking.service;

import java.util.Optional;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cabbooking.model.AbstractUser;
import com.cabbooking.model.Admin;
import com.cabbooking.model.Customer;
import com.cabbooking.model.Driver;
import com.cabbooking.model.PasswordResetToken;
import com.cabbooking.repository.AdminRepository;
import com.cabbooking.repository.CustomerRepository;
import com.cabbooking.repository.DriverRepository;
import com.cabbooking.repository.PasswordResetTokenRepository;

/*
 * Service class for password reset functionality.
 * Implements IPasswordResetService interface.
 * 
 * Main Responsibilities:
 * - Create and send password reset tokens to users.
 * - Reset passwords for users based on password reset tokens.
 * 
 * Dependencies:
 * - IProfileService for user lookup.
 * - PasswordResetTokenRepository for storing and retrieving password reset tokens.
 * - IEmailService for sending emails.
 * - PasswordEncoder for encoding and decoding passwords.
 * - AdminRepository, CustomerRepository, and DriverRepository for user type-specific operations.
 */
@Service
public class PasswordResetServiceImpl implements IPasswordResetService {

    /*
     * Provides access to the profile service for user lookup.
     */
    @Autowired
    private IProfileService profileService; // Reuse the profile service to find users

    /*
     * Provides access to the password reset token repository.
     */
    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    /*
     * Provides access to the admin repository.
     */
    @Autowired
    private AdminRepository adminRepository;

    /*
     * Provides access to the customer repository.
     */
    @Autowired
    private CustomerRepository customerRepository;

    /*
     * Provides access to the driver repository.
     */
    @Autowired
    private DriverRepository driverRepository;

    /*
     * Provides access to the email service.
     */
    @Autowired
    private IEmailService emailService;

    /*
     * Provides access to the password encoder.
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /*
     * Creates a password reset token and sends an email to the user with a link to reset their password.
     * 
     * Workflow:
     * - Email is received from the user
     * - A user is searched for by email
     * - A password reset token is created and saved to the database
     * - An email is sent to the user with a link to reset their password
     * 
     * @param email The email address of the user to send the password reset link to.
     */
    @Override
    @Transactional
    public void createAndSendPasswordResetToken(String email) {
        AbstractUser user = findUserByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User associated with token not found."));

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, LocalDateTime.now().plusHours(1), user.getEmail());
        tokenRepository.save(resetToken);

        String subject = "Your Password Reset Request";
        String resetUrl = "http://your-frontend-url/reset-password?token=" + token;
        String message = "To reset your password, click the link below:\n" + resetUrl;
        
        emailService.sendSimpleEmail(user.getEmail(), subject, message);
    }

    /*
     * Resets the password for a user based on a password reset token.
     * 
     * Workflow:
     * - Token and new password are received from the user
     * - Token is validated
     * - User associated with the token is found
     * - Password is updated for the user
     * - Token is invalidated
     * - True is returned
     * 
     * @param token The password reset token.
     * @param newPassword The new password to set for the user.
     * @return True if the password was successfully reset, false otherwise.
     */
    @Override
    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token);
        if (resetToken == null || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return false; // Token is invalid or expired
        }

        AbstractUser user = findUserByEmail(resetToken.getUserEmail())
                .orElseThrow(() -> new IllegalStateException("User associated with token not found."));

        user.setPassword(passwordEncoder.encode(newPassword));
        saveUser(user);

        tokenRepository.delete(resetToken); // Invalidate the token after use
        return true;
    }

    /*
     * Helper method to find a user by email address.
     * 
     * @param email The email address of the user to find.
     * @return An Optional containing the user if found, empty otherwise.
     */
    private Optional<AbstractUser> findUserByEmail(String email) {
        return adminRepository.findByEmail(email)
                .<AbstractUser>map(admin -> admin)
                .or(() -> customerRepository.findByEmail(email).map(customer -> customer))
                .or(() -> driverRepository.findByEmail(email).map(driver -> driver));
    }

    /**
     * Helper method to save a user to the correct repository based on their type.
     * 
     * @param user The user object to save.
     */
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