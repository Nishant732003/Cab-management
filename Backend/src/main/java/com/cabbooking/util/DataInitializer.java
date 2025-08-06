package com.cabbooking.util;

import com.cabbooking.model.Admin;
import com.cabbooking.repository.AdminRepository;
import com.cabbooking.repository.CustomerRepository;
import com.cabbooking.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.cabbooking.model.Customer;
import com.cabbooking.model.Driver;

/**
 * DataInitializer is a Spring Component implementing CommandLineRunner to seed initial data
 * into the database when the Spring Boot application starts.
 * 
 * This class is useful for development and testing environments to ensure essential data
 * like the superadmin account and some sample users exist at startup.
 * 
 * Key Features:
 * - Creates a special superadmin user with username "harshit" if not already present.
 * - Adds a default unverified admin user if none exist.
 * - Creates sample customer and driver records for testing purposes.
 * - Passwords are securely hashed using the injected PasswordEncoder before saving.
 * 
 * IMPORTANT:
 * - This initializer runs on every application startup.
 * - It checks for existing entries before inserting to avoid duplicates.
 * - In production, you may want to disable this or use more sophisticated data migration tools.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AdminRepository adminRepo;

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private DriverRepository driverRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Called by Spring Boot after the application context is loaded.
     * Seeds initial data by checking and inserting base users.
     *
     * @param args Optional command line arguments
     * @throws Exception if any error occurs during data seeding
     */
    @Override
    public void run(String... args) throws Exception {
        // Seed superadmin user "harshit" with verified status
        if (adminRepo.findByUsername("harshit") == null) {
            Admin superadmin = new Admin();
            superadmin.setUsername("harshit");
            // Hash the password securely before saving
            superadmin.setPassword(passwordEncoder.encode("SuperSecret123")); // use a strong password here
            superadmin.setVerified(true);
            adminRepo.save(superadmin);
        }

        // Seed a default admin if no admins exist in the system
        if (adminRepo.count() == 0) {
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("adminpass"));
            admin.setVerified(false);  // default new admins are unverified
            adminRepo.save(admin);
        }

        // Seed a sample customer if none exists
        if (customerRepo.count() == 0) {
            Customer customer = new Customer();
            customer.setUsername("customer");
            customer.setPassword(passwordEncoder.encode("custpass"));
            customerRepo.save(customer);
        }

        // Seed a sample driver if none exists
        if (driverRepo.count() == 0) {
            Driver driver = new Driver();
            driver.setUsername("driver");
            driver.setPassword(passwordEncoder.encode("driverpass"));
            driver.setLicenceNo("LIC123");   // sample license number
            driver.setRating(4.5f);           // sample driver rating
            driver.setVerified(false);        // unverified by default
            driverRepo.save(driver);
        }
    }
}
