package com.cabbooking.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cabbooking.model.Cab;
import com.cabbooking.model.Driver;
import com.cabbooking.model.TripBooking;
import com.cabbooking.model.TripStatus;
import com.cabbooking.repository.DriverRepository;
import com.cabbooking.repository.TripBookingRepository;

/**
 * A background service responsible for processing scheduled trips. This service
 * uses Spring's @Scheduled annotation to run a task at a fixed interval,
 * ensuring that future bookings are handled automatically without manual
 * intervention.
 *
 * Main Responsibilities: 
 * - Checks for scheduled trips that are due to start within the next 15 minutes. 
 * - Assigns drivers and cabs to these trips if available.
 *
 * Security: 
 * - This service is only accessible to users with the 'Admin' role.
 */
@Service
public class TripSchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(TripSchedulerService.class);

    @Autowired
    private TripBookingRepository tripBookingRepository;

    @Autowired
    private DriverRepository driverRepository;

    /**
     * This method is the core of the scheduler. It runs automatically at a
     * fixed interval to find and process scheduled trips that are due soon. *
     * The @Scheduled(fixedRate = 60000) annotation tells Spring to execute this
     * method every 60,000 milliseconds (i.e., every 1 minute). * The
     *
     * @Transactional annotation ensures that all database operations within
     * this method are part of a single transaction. If any part fails, all
     * changes are rolled back.
     *
     * Workflow:
     * - This method is scheduled to run every 1 minute.
     * - It checks for scheduled trips that are due to start within the next 15 minutes.
     * - If a trip is due, it attempts to find an available driver and assign them to the trip.
     * - If driver is found, the trip status is updated to 'IN_PROGRESS'.
     */
    @Scheduled(fixedRate = 60000) // Runs every 1 minute
    @Transactional
    public void assignDriversToScheduledTrips() {
        logger.info("Scheduler running: Checking for scheduled trips...");

        // Find all trips that are currently in the 'SCHEDULED' state and are due to start
        // within the next 15 minutes. This gives the system a small buffer to find a driver.
        List<TripBooking> dueTrips = tripBookingRepository.findAll().stream()
                .filter(trip -> trip.getStatus() == TripStatus.SCHEDULED
                && trip.getFromDateTime().isBefore(LocalDateTime.now().plusMinutes(15)))
                .collect(Collectors.toList());

        // If no trips are due, log it and exit the method to save resources.
        if (dueTrips.isEmpty()) {
            logger.info("No due scheduled trips found.");
            return;
        }

        // Iterate through each due trip and attempt to assign a driver and cab.
        for (TripBooking trip : dueTrips) {
            logger.info("Attempting to assign driver to scheduled trip ID: {}", trip.getTripBookingId());
            try {
                // Find the best driver whose cab's carType matches the trip's required carType
                Driver bestAvailableDriver = driverRepository.findAll().stream()
                        .filter(d -> 
                            d.getVerified() && 
                            d.getIsAvailable() &&
                            d.getCab() != null &&
                            d.getCab().getCarType().equalsIgnoreCase(trip.getCarType()) // Use the carType from the trip
                        )
                        .max(Comparator.comparing(Driver::getRating))
                        .orElse(null);

                // If a driver was found, assign them and their cab to the trip.
                if (bestAvailableDriver != null) {
                    Cab assignedCab = bestAvailableDriver.getCab();

                    // Mark the driver and their cab as unavailable
                    bestAvailableDriver.setIsAvailable(false);
                    assignedCab.setIsAvailable(false);
                    driverRepository.save(bestAvailableDriver);

                    // Update the trip details
                    trip.setDriver(bestAvailableDriver);
                    trip.setCab(assignedCab);
                    trip.setStatus(TripStatus.CONFIRMED);
                    tripBookingRepository.save(trip);
                    
                    logger.info("Successfully assigned Driver {} and Cab {} to Trip {}", bestAvailableDriver.getId(), assignedCab.getCabId(), trip.getTripBookingId());
                } else {
                    logger.warn("Could not find an available driver for scheduled trip ID: {}", trip.getTripBookingId());
                }
            } catch (Exception e) {
                // Log any unexpected errors to prevent the scheduler from crashing.
                logger.error("Error while assigning driver to scheduled trip ID: {}", trip.getTripBookingId(), e);
            }
        }
    }
}
