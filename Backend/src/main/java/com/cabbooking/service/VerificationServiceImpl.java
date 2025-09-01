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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

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
     * Workflow: Sends a verification link to a user's email.
     * 1.  Finds the user by email across all user types.
     * 2.  Checks if the user's email is already verified.
     * 3.  Generates a new, unique verification token with a 24-hour expiry.
     * 4.  Saves the token to the database.
     * 5.  Constructs a verification URL and sends it to the user's email.
     *
     * Exception Handling:
     * - Throws IllegalArgumentException if no user is found for the given email.
     * - Throws IllegalStateException if the email is already verified.
     */
    @Override
    @Transactional
    public void sendVerificationLink(String email) {
        AbstractUser user = findUserByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("User with email " + email + " not found."));

        if (Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new IllegalStateException("Email is already verified.");
        }

        String token = UUID.randomUUID().toString();
        // This constructor matches your 'earlier code' structure
        VerificationToken verificationToken = new VerificationToken(token, LocalDateTime.now().plusHours(24), user.getEmail());
        tokenRepository.save(verificationToken);

        String subject = "Verify Your Email for Cab Booking";
        String verificationUrl = "http://localhost:8080/api/auth/verify-email?token=" + token;
        String message = "Please click the following link to verify your email address:\n" + verificationUrl;

        // This method call matches your 'earlier code' structure
        emailService.sendSimpleEmail(user.getEmail(), subject, message);
    }

    /**
     * Workflow: Verifies a user's email address using a token.
     * 1.  Finds the token in the database.
     * 2.  Checks if the token is valid and not expired.
     * 3.  Finds the user associated with the token's email.
     * 4.  Sets the user's emailVerified status to true.
     * 5.  Saves the updated user.
     * 6.  Deletes the used token from the database to prevent reuse.
     */
    @Override
    @Transactional
    public boolean verifyToken(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token);
        if (verificationToken == null || verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return false; // Token is invalid or expired
        }

        String userEmail = verificationToken.getUserEmail();
        AbstractUser user = findUserByEmail(userEmail)
            .orElseThrow(() -> new IllegalArgumentException("User associated with token not found."));

        user.setEmailVerified(true);
        saveUser(user); // Use the helper method to save the user

        tokenRepository.delete(verificationToken);
        return true;
    }

    // This is the correct way to find a user by email using Optional, as in your original file.
    private Optional<AbstractUser> findUserByEmail(String email) {
        // Correctly search each repository and chain them with .or()
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