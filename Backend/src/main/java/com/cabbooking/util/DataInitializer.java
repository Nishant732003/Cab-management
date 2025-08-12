package com.cabbooking.util;

import com.cabbooking.model.Admin;
import com.cabbooking.model.Cab;
import com.cabbooking.repository.AdminRepository;
import com.cabbooking.repository.CabRepository;
import com.cabbooking.repository.CustomerRepository;
import com.cabbooking.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.cabbooking.model.Customer;
import com.cabbooking.model.Driver;

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
        // Seed superadmin user "harshit" with verified status
        if (adminRepo.findByUsername("harshit") == null) {
            Admin superadmin = new Admin();
            superadmin.setUsername("harshit");
            superadmin.setPassword(passwordEncoder.encode("SuperSecret123"));
            superadmin.setVerified(true);
            adminRepo.save(superadmin);
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
            driver.setLicenceNo("LIC123");
            driver.setRating(4.5f);
            driver.setVerified(true); // Set to true for testing booking
            driverRepo.save(driver);
        }
        
        // Seed a sample cab
        if (cabRepo.count() == 0) {
            Cab cab = new Cab();
            cab.setCarType("Sedan");
            cab.setPerKmRate(15.0f);
            cabRepo.save(cab);
        }
    }
}
