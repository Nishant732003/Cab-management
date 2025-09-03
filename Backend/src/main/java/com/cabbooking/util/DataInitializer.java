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
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Seed superadmin user "Super" with verified status
        if (adminRepo.findByUsername("Super") == null) {
            Admin superadmin = new Admin();
            superadmin.setUsername("Super");
            superadmin.setPassword(passwordEncoder.encode("superpass"));
            superadmin.setEmail("super@gmail.com");
            superadmin.setVerified(true); // Superadmin is always verified
            adminRepo.save(superadmin);
        }

        // Seed admin user "Admin" without verified status
        if (adminRepo.findByUsername("Admin") == null) {
            Admin admin = new Admin();
            admin.setUsername("Admin");
            admin.setPassword(passwordEncoder.encode("adminpass"));
            admin.setEmail("admin@gmail.com");
            // 'verified' defaults to false, so no need to set it
            adminRepo.save(admin);
        }

        // Seed customer user "Customer"
        if (customerRepo.findByUsername("Customer") == null) {
            Customer customer = new Customer();
            customer.setUsername("Customer");
            customer.setPassword(passwordEncoder.encode("custpass"));
            customer.setEmail("customer@gmail.com");
            customerRepo.save(customer);
        }

        // Seed driver user "Driver" and associate a Cab
        if (driverRepo.findByUsername("Driver") == null) {
            // 1. Create the Driver
            Driver driver = new Driver();
            driver.setUsername("Driver");
            driver.setPassword(passwordEncoder.encode("driverpass"));
            driver.setEmail("driver@gmail.com");
            driver.setLicenceNo("LIC123");
            driver.setRating(4.5f);
            driver.setVerified(true);
            driver.setIsAvailable(true);
            driver.setTotalRatings(10);

            // 2. Create the Cab
            Cab cab = new Cab();
            cab.setCarType("Sedan");
            cab.setPerKmRate(15.0f);
            cab.setNumberPlate("MH12AB1234");
            cab.setIsAvailable(true);

            // 3. Establish the bidirectional link
            driver.setCab(cab);
            cab.setDriver(driver);

            // 4. Save the driver. The associated cab will be saved automatically due to CascadeType.ALL.
            driverRepo.save(driver);
        }
    }
}
