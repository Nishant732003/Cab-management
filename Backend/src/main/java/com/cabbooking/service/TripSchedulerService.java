package com.cabbooking.service;

import com.cabbooking.model.Cab;
import com.cabbooking.model.Driver;
import com.cabbooking.model.TripBooking;
import com.cabbooking.model.TripStatus;
import com.cabbooking.repository.CabRepository;
import com.cabbooking.repository.DriverRepository;
import com.cabbooking.repository.TripBookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A background service responsible for processing scheduled trips.
 * This service uses Spring's @Scheduled annotation to run a task at a fixed interval,
 * ensuring that future bookings are handled automatically without manual intervention.
 */
@Service
public class TripSchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(TripSchedulerService.class);

    @Autowired
    private TripBookingRepository tripBookingRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private CabRepository cabRepository;

    /**
     * This method is the core of the scheduler. It runs automatically at a fixed interval
     * to find and process scheduled trips that are due soon.
     * * The @Scheduled(fixedRate = 60000) annotation tells Spring to execute this method
     * every 60,000 milliseconds (i.e., every 1 minute).
     * * The @Transactional annotation ensures that all database operations within this method
     * are part of a single transaction. If any part fails, all changes are rolled back.
     */
    @Scheduled(fixedRate = 60000) // Runs every 1 minute
    @Transactional
    public void assignDriversToScheduledTrips() {
        logger.info("Scheduler running: Checking for scheduled trips...");

        // 1. Find all trips that are currently in the 'SCHEDULED' state and are due to start
        //    within the next 15 minutes. This gives the system a small buffer to find a driver.
        List<TripBooking> dueTrips = tripBookingRepository.findAll().stream()
                .filter(trip -> trip.getStatus() == TripStatus.SCHEDULED &&
                               trip.getFromDateTime().isBefore(LocalDateTime.now().plusMinutes(15)))
                .collect(Collectors.toList());

        // If no trips are due, log it and exit the method to save resources.
        if (dueTrips.isEmpty()) {
            logger.info("No due scheduled trips found.");
            return;
        }

        // 2. Iterate through each due trip and attempt to assign a driver and cab.
        for (TripBooking trip : dueTrips) {
            logger.info("Attempting to assign driver to scheduled trip ID: {}", trip.getTripBookingId());
            try {
                // 3. Find the best available driver (verified, available, and highest rating).
                Driver bestAvailableDriver = driverRepository.findAll().stream()
                        .filter(d -> d.getVerified() && d.getIsAvailable())
                        .max(Comparator.comparing(Driver::getRating))
                        .orElse(null); // Return null if no driver is found

                // 4. Find any available cab.
                Cab availableCab = cabRepository.findAll().stream()
                        .filter(Cab::getIsAvailable)
                        .findFirst()
                        .orElse(null); // Return null if no cab is found

                // 5. If both a driver and a cab were found, assign them to the trip.
                if (bestAvailableDriver != null && availableCab != null) {
                    // Mark the driver and cab as unavailable for other trips.
                    bestAvailableDriver.setIsAvailable(false);
                    availableCab.setIsAvailable(false);
                    driverRepository.save(bestAvailableDriver);
                    cabRepository.save(availableCab);

                    // Update the trip with the assigned driver and cab.
                    trip.setDriver(bestAvailableDriver);
                    trip.setCab(availableCab);
                    // Change the trip status from SCHEDULED to CONFIRMED.
                    trip.setStatus(TripStatus.CONFIRMED);
                    tripBookingRepository.save(trip);
                    
                    logger.info("Successfully assigned Driver {} and Cab {} to Trip {}", bestAvailableDriver.getId(), availableCab.getCabId(), trip.getTripBookingId());
                } else {
                    // Log a warning if no resources are available. The trip will be re-checked on the next run.
                    logger.warn("Could not find available driver or cab for scheduled trip ID: {}", trip.getTripBookingId());
                }
            } catch (Exception e) {
                // Log any unexpected errors to prevent the scheduler from crashing.
                logger.error("Error while assigning driver to scheduled trip ID: {}", trip.getTripBookingId(), e);
            }
        }
    }
}
