package com.cabbooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan; // FIX: Import ComponentScan
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
// FIX: Add this annotation to ensure all controllers, services, etc., are found.
@ComponentScan(basePackages = "com.cabbooking") 
public class CabBookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(CabBookingApplication.class, args);
    }
}

