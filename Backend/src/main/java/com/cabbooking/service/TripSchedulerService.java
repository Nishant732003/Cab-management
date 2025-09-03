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

    /*
     * Constant for nearby radius in kilometers
     */
    private static final double NEARBY_RADIUS_KM = 5.0;


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
     * - If a trip is due, it attempts to find an best available nearby driver and assign them to the trip.
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

        // Find all available drivers once to avoid multiple DB calls
        List<Driver> allAvailableDrivers = driverRepository.findAll().stream()
                .filter(d -> d.getVerified() && d.getIsAvailable() && d.getCab() != null &&
                              d.getLatitude() != null && d.getLongitude() != null)
                .toList();

        // Iterate through each due trip and attempt to assign a driver and cab.
        for (TripBooking trip : dueTrips) {

            if (trip.getFromLatitude() == null || trip.getFromLongitude() == null) {
                logger.warn("Scheduled trip ID: {} is missing starting coordinates. Skipping.", trip.getTripBookingId());
                continue;
            }

            logger.info("Attempting to assign driver to scheduled trip ID: {}", trip.getTripBookingId());

            try {
                // Find best driver who is nearby the trip's STARTING location
                Driver bestAvailableDriver = allAvailableDrivers.stream()
                        .filter(d -> d.getCab().getCarType().equalsIgnoreCase(trip.getCarType()))
                        .filter(d -> calculateDistance(
                                trip.getFromLatitude(),
                                trip.getFromLongitude(),
                                d.getLatitude(),
                                d.getLongitude()) <= NEARBY_RADIUS_KM)
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

    /*
     * Helper method to calculate distance between two coordinates
     */
    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        final int R = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lng2 - lng1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
