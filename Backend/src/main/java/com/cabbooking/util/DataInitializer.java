package com.cabbooking.util;

import com.cabbooking.model.Customer;
import com.cabbooking.model.Admin;
import com.cabbooking.model.Driver;
import com.cabbooking.repository.CustomerRepository;
import com.cabbooking.repository.AdminRepository;
import com.cabbooking.repository.DriverRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * DataInitializer is a Spring component that runs once on application startup.
 * 
 * Its purpose is to seed sample data into the database for development and testing.
 * 
 * Specifically, it checks if no Admin, Customer, or Driver records exist,
 * and if so, inserts default sample users with preset credentials.
 * 
 * This ensures the application has initial data to work with,
 * particularly useful for testing authentication and basic operations.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final AdminRepository adminRepo;
    private final CustomerRepository customerRepo;
    private final DriverRepository driverRepo;

    /**
     * Constructor-based dependency injection ensures repository instances are available.
     */
    public DataInitializer(AdminRepository adminRepo,
                           CustomerRepository customerRepo,
                           DriverRepository driverRepo) {
        this.adminRepo = adminRepo;
        this.customerRepo = customerRepo;
        this.driverRepo = driverRepo;
    }

    /**
     * This method runs after application context initialization.
     * It inserts sample Admin, Customer, and Driver data if none exists.
     *
     * @param args command line arguments (ignored here)
     * @throws Exception any startup exception
     */
    @Override
    public void run(String... args) throws Exception {
        if (adminRepo.count() == 0) {
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword("adminpass");
            adminRepo.save(admin);
        }

        if (customerRepo.count() == 0) {
            Customer customer = new Customer();
            customer.setUsername("customer");
            customer.setPassword("custpass");
            customerRepo.save(customer);
        }

        if (driverRepo.count() == 0) {
            Driver driver = new Driver();
            driver.setUsername("driver");
            driver.setPassword("driverpass");
            driver.setLicenceNo("LIC123");
            driver.setRating(4.5f);
            driverRepo.save(driver);
        }
    }
}
