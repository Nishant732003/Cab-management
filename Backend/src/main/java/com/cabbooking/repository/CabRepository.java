package com.cabbooking.repository;

import com.cabbooking.model.Cab;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CabRepository extends JpaRepository<Cab, Integer> {

    // Custom method to find all cabs of a specific type
    List<Cab> findByCarType(String carType);
}