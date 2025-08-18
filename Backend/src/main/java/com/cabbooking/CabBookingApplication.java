package com.cabbooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main entry point for the Cab Booking Platform Spring Boot application.
 * 
 * Responsibilities:
 * - Bootstraps and starts the Spring application context.
 * - Enables auto-configuration, component scanning, and configuration properties
 *   via the @SpringBootApplication annotation (which is a convenience annotation
 *   combining @Configuration, @EnableAutoConfiguration, and @ComponentScan).
 * 
 * When run, this class launches the embedded web server (e.g., Tomcat) and prepares
 * the application to handle HTTP requests.
 * 
 * Other modules like Login, Admin, Customer, Driver modules, services, and repositories
 * will be automatically discovered and wired due to component scanning starting from this package.
 */
@SpringBootApplication
@EnableScheduling
public class CabBookingApplication {

    /**
     * Main method invoked to launch the Spring Boot application.
     * 
     * @param args command-line arguments passed to the application (optional).
     */
    public static void main(String[] args) {
        SpringApplication.run(CabBookingApplication.class, args);
    }
}
