package com.cabbooking.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.cabbooking.model.Admin;
import com.cabbooking.model.Cab;
import com.cabbooking.model.Customer;
import com.cabbooking.model.Driver;
import com.cabbooking.repository.AdminRepository;
import com.cabbooking.repository.CabRepository;
import com.cabbooking.repository.CustomerRepository;
import com.cabbooking.repository.DriverRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AdminRepository adminRepo;

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private DriverRepository driverRepo;
    
    @Autowired
    private CabRepository cabRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Seed superadmin user "super" with verified status
        if (adminRepo.findByUsername("Super") == null) {
            Admin superadmin = new Admin();
            superadmin.setUsername("Super");
            superadmin.setPassword(passwordEncoder.encode("superpass"));
            superadmin.setEmail("super@gmail.com");
            // Set first and last names
            superadmin.setFirstName("Super");
            superadmin.setLastName("Admin");
            superadmin.setVerified(true);
            adminRepo.save(superadmin);
        }

        // Seed admin user "Admin" without verified status
        if (adminRepo.findByUsername("Admin") == null) {
            Admin admin = new Admin();
            admin.setUsername("Admin");
            admin.setPassword(passwordEncoder.encode("adminpass"));
            admin.setEmail("admin@gmail.com");
            // Set first and last names
            admin.setFirstName("Regular");
            admin.setLastName("Admin");
            adminRepo.save(admin);
        }

        // Seed customer user "Customer"
        if (customerRepo.findByUsername("Customer") == null) {
            Customer customer = new Customer();
            customer.setUsername("Customer");
            customer.setPassword(passwordEncoder.encode("custpass"));
            customer.setEmail("customer@gmail.com");
            // Set first and last names
            customer.setFirstName("John");
            customer.setLastName("Doe");
            customerRepo.save(customer);
        }

        // Seed driver user "Driver" with verified status
        if (driverRepo.findByUsername("Driver") == null) {
            Driver driver = new Driver();
            driver.setUsername("Driver");
            driver.setPassword(passwordEncoder.encode("driverpass"));
            driver.setEmail("driver@gmail.com");
            driver.setLicenceNo("LIC123");
            // Set first and last names
            driver.setFirstName("Jane");
            driver.setLastName("Smith");
            driver.setRating(4.5f);
            driver.setVerified(true); // Set to true for testing booking
            driver.setIsAvailable(true); // Set to true for testing booking
            driver.setTotalRatings(10); // Set total ratings for testing
            driverRepo.save(driver);
        }
        
        // Seed cab instance "Sedan" with perKmRate
        if (cabRepo.count() == 0) {
            Cab cab = new Cab();
            cab.setCarType("Sedan");
            cab.setPerKmRate(15.0f);
            cab.setIsAvailable(true);
            cabRepo.save(cab);
        }
    }
}