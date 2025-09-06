package com.cabbooking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cabbooking.dto.DriverRegistrationRequest;
import com.cabbooking.model.Cab;
import com.cabbooking.model.Driver;
import com.cabbooking.repository.AdminRepository;
import com.cabbooking.repository.CustomerRepository;
import com.cabbooking.repository.DriverRepository;

@Service
public class DriverRegistrationServiceImpl implements IDriverRegistrationService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Driver registerDriver(DriverRegistrationRequest request) {

        if (adminRepository.existsByUsername(request.getUsername())
        || customerRepository.existsByUsername(request.getUsername())
        || driverRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already taken.");
        }

        if (adminRepository.existsByEmail(request.getEmail())
        || customerRepository.existsByEmail(request.getEmail())
        || driverRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        boolean licenceExists = driverRepository.findAll().stream()
            .anyMatch(d -> d.getLicenceNo() != null && d.getLicenceNo().equalsIgnoreCase(request.getLicenceNo()));
        if (licenceExists) {
            throw new IllegalArgumentException("Licence number is already registered.");
        }

        Driver driver = new Driver();
        driver.setUsername(request.getUsername());

        // --- ADDED: Set first name and last name from the request DTO ---
        driver.setFirstName(request.getFirstName());
        driver.setLastName(request.getLastName());

        driver.setPassword(passwordEncoder.encode(request.getPassword()));
        driver.setEmail(request.getEmail());
        driver.setAddress(request.getAddress());
        driver.setMobileNumber(request.getMobileNumber());
        driver.setLicenceNo(request.getLicenceNo());

        driver.setVerified(false);
        driver.setRating(request.getRating());
        driver.setTotalRatings(request.getTotalRatings());
        driver.setLatitude(request.getLatitude());
        driver.setLongitude(request.getLongitude());

        Cab cab = new Cab();
        cab.setDriver(driver);
        driver.setCab(cab);

        return driverRepository.save(driver);
    }
}