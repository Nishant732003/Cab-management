package com.cabbooking.service;

import com.cabbooking.model.Driver;
import java.util.List;

// You should create this interface if it's not already there from the PDF plan
public interface IDriverService {
    // We will add other methods like insertDriver, updateDriver later...

    List<Driver> viewBestDrivers();

    // ==> ADD THESE NEW METHODS <==
    List<Driver> viewUnverifiedDrivers();

    Driver verifyDriver(int driverId);
}