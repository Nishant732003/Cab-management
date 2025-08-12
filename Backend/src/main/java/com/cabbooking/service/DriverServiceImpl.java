package com.cabbooking.service;

import com.cabbooking.model.Cab;
import com.cabbooking.model.Driver;
import com.cabbooking.repository.CabRepository;
import com.cabbooking.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DriverServiceImpl implements IDriverService {

    @Autowired
    private DriverRepository driverRepository;

    @Override
    public List<Driver> viewBestDrivers() {
        // As per the PDF, best drivers have a rating of 4.5 or higher
        return driverRepository.findAll().stream()
                .filter(driver -> driver.getRating() != null && driver.getRating() >= 4.5f)
                .collect(Collectors.toList());
    }

    // We will implement other driver-related methods here later...
    // ==> ADD IMPLEMENTATION FOR THE NEW METHODS <==

    @Override
    public List<Driver> viewUnverifiedDrivers() {
        return driverRepository.findAll().stream()
                .filter(driver -> driver.getVerified() != null && !driver.getVerified())
                .collect(Collectors.toList());
    }

    @Override
    public Driver verifyDriver(int driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found with id: " + driverId));
        
        driver.setVerified(true);
        return driverRepository.save(driver);
    }
}
