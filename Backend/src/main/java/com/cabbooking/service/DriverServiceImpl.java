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

    @Autowired
    private CabRepository cabRepository;

    @Override
    public Driver assignCabToDriver(int driverId, int cabId) {
        // 1. Find the driver
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found with id: " + driverId));

        // 2. Find the cab
        Cab cab = cabRepository.findById(cabId)
                .orElseThrow(() -> new IllegalArgumentException("Cab not found with id: " + cabId));

        // 3. Assign the cab to the driver
        driver.setCab(cab);

        // 4. Save the updated driver record
        return driverRepository.save(driver);
    }

    @Override
    public List<Driver> viewBestDrivers() {
        // As per the PDF, best drivers have a rating of 4.5 or higher
        return driverRepository.findAll().stream()
                .filter(driver -> driver.getRating() != null && driver.getRating() >= 4.5f)
                .collect(Collectors.toList());
    }

    // We will implement other driver-related methods here later...
}
